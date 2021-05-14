package de.micromata.merlin.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellRangeAddress

/**
 * A helper wrapper for creating rows in a more convenient way.
 */
class ExcelRow(val sheet: ExcelSheet, val row: Row) {
    private val cellMap: MutableMap<Int, ExcelCell> = HashMap()

    /**
     * Get row number this row represents
     *
     * @return the row number (0 based)
     */
    val rowNum: Int
        get() = row.rowNum

    /**
     * @param columnName Registered column definition.
     * @param type       Only used, if new cell will be created.
     * @return The (created) cell. If column definition isn't known, an IllegalArgumentException will be thrown.
     */
    @JvmOverloads
    fun getCell(columnName: String, type: ExcelCellType? = null): ExcelCell? {
        val columnDef = sheet.getColumnDef(columnName) ?: return null
        return getCell(columnDef.columnNumber, type)
    }

    /**
     * @param columnDef  Registered column definition.
     * @param type       Only used, if new cell will be created.
     * @return The (created) cell. If column definition isn't known, an IllegalArgumentException will be thrown.
     */
    @JvmOverloads
    fun getCell(columnDef: ExcelColumnDef, type: ExcelCellType? = null): ExcelCell {
        sheet.findAndReadHeadRow()
        return getCell(columnDef.columnNumber, type)
    }

    /**
     * @param columnNumber The column number (0-based).
     * @param type         Only used, if new cell will be created.
     * @return The (created) cell, not null.
     */
    @JvmOverloads
    fun getCell(columnNumber: Int, type: ExcelCellType? = null): ExcelCell {
        var excelCell = cellMap[columnNumber]
        var lastCellNum = (row.lastCellNum - 1).toShort()
        if (lastCellNum < 0) {
            lastCellNum = 0
        }
        if (excelCell == null) {
            if (columnNumber <= lastCellNum) {
                return ensureCell(columnNumber, type)
            }
            for (colNum in lastCellNum..columnNumber) {
                excelCell = ensureCell(colNum, type)
            }
        }
        return excelCell!!
    }

    /**
     * Assumes [ExcelCellType.STRING]
     *
     * @return The created cell.
     */
    @JvmOverloads
    fun createCell(type: ExcelCellType? = ExcelCellType.STRING): ExcelCell {
        var colCount = row.lastCellNum.toInt()
        if (colCount < 0) {
            colCount = 0
        }
        return ensureCell(colCount, type)
    }

    private fun ensureCell(columnIndex: Int, type: ExcelCellType?): ExcelCell {
        var excelCell = cellMap[columnIndex]
        if (excelCell == null) {
            var cell = row.getCell(columnIndex)
            if (cell == null) {
                cell = row.createCell(columnIndex, if (type != null) type.cellType else CellType.STRING)
                excelCell = ensureCell(cell, false)
            } else {
                excelCell = ensureCell(cell, true)
            }
        }
        return excelCell
    }

    private fun ensureCell(cell: Cell, existingPoiCell: Boolean): ExcelCell {
        var excelCell = cellMap[cell.columnIndex]
        if (excelCell == null) {
            excelCell = ExcelCell(this, cell, existingPoiCell)
            cellMap[cell.columnIndex] = excelCell
        }
        return excelCell
    }

    fun createCells(vararg cells: String) {
        createCells(null, *cells)
    }

    fun createCells(cellStyle: CellStyle?, vararg cells: String?) {
        for (cellString in cells) {
            val cell = createCell(ExcelCellType.STRING)
            cell.setCellValue(cellString)
            if (cellStyle != null) {
                cell.setCellStyle(cellStyle)
            }
        }
    }

    fun setHeight(height: Float): ExcelRow {
        row.heightInPoints = height
        return this
    }

    fun addMergeRegion(fromCol: Int, toCol: Int) {
        val range = CellRangeAddress(row.rowNum, row.rowNum, fromCol, toCol)
        row.sheet.addMergedRegion(range)
    }

    /**
     * Duplicates the current row.
     * @param insertPosition The row num where the copied row should be inserted to (default is after this row).
     * @return Duplicated ExcelRow
     */
    @JvmOverloads
    fun copyAndInsert(targetSheet: ExcelSheet? = null, insertPosition: Int = rowNum + 1): ExcelRow {
        val target = targetSheet ?: sheet
        if (insertPosition <= target.poiSheet.lastRowNum) {
            target.shiftRows(insertPosition, n = 1)
        }
        val newPoiRow = target.poiSheet.createRow(insertPosition)
        val newRow = ExcelRow(target, newPoiRow)
        newRow.copyCellsFrom(this)
        target.clearRowMap()
        return newRow
    }

    fun copyCellsFrom(srcRow: ExcelRow) {
        val numMergedRegions = mutableListOf<CellRangeAddress>()
        srcRow.sheet.poiSheet.mergedRegions?.forEach { address ->
            // Only merge region in this single row are supported.
            if (address.firstRow == srcRow.rowNum && address.lastRow == srcRow.rowNum) {
                numMergedRegions.add(address)
            }
        }
        for (colNum in 0..srcRow.lastCellNum) {
            val srcCell = srcRow.row.getCell(colNum)
            if (srcCell != null) {
                val destCell = row.getCell(colNum) ?: row.createCell(colNum)
                ExcelCell.copyCell(srcCell, destCell)
                numMergedRegions.filter { it.firstColumn == srcCell.columnIndex }.forEach {
                    sheet.addMergeRegion(CellRangeAddress(this.rowNum, this.rowNum, it.firstColumn, it.lastColumn))
                }
            }
        }
    }

    /**
     * Fills a row automatically by using properties of given obj, if matched by column head or any alias.
     */
    @Suppress("unused")
    fun autoFillFromObject(obj: Any?, vararg ignoreProperties: String) {
        autoFillFromObject(obj, { _, _, _, _ -> false }, *ignoreProperties)
    }

    /**
     * Fills a row automatically by using properties of given obj, if matched by column head or any alias.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun autoFillFromObject(
        obj: Any?,
        process: (Any, Any, ExcelCell, ExcelColumnDef) -> Boolean,
        vararg ignoreProperties: String
    ) {
        obj ?: return
        for (colDef in sheet.columnDefinitions) {
            if (sheet.cache.autoFillCache.notFoundFieldsSet.contains(colDef)) {
                // Don't search multiple times, if no method was found.
                continue
            }
            var searchResult = getPropertyValue(obj, colDef, colDef.columnHeadname, ignoreProperties)
            if (searchResult != null) {
                processPropertyValue(obj, searchResult, colDef, process)
                continue
            }
            for (alias in colDef.columnAliases) {
                searchResult = getPropertyValue(obj, colDef, alias.decapitalize(), ignoreProperties)
                if (searchResult != null) {
                    processPropertyValue(obj, searchResult, colDef, process)
                    break
                }
            }
            if (searchResult == null) {
                // No bean property found for this column definition.
                sheet.cache.autoFillCache.notFoundFieldsSet.add(colDef)
            }
        }
    }

    /**
     * Creates row from all registered [ExcelColumnDef].
     * @return this for chaining.
     */
    fun fillHeadRow(): ExcelRow {
        sheet.columnDefinitions.forEachIndexed { idx, def ->
            ensureCell(idx, ExcelCellType.STRING).setCellValue(def.columnHeadname)
        }
        return this
    }

    private fun processPropertyValue(
        obj: Any,
        searchResult: PropertySearchResult,
        colDef: ExcelColumnDef,
        process: (Any, Any, ExcelCell, ExcelColumnDef) -> Boolean
    ) {
        if (searchResult.propertyIgnored || searchResult.value == null) {
            return
        }
        val cell = getCell(colDef)
        if (!process(obj, searchResult.value, cell, colDef)) {
            cell.setCellValue(searchResult.value)
        }
    }

    /**
     * @return null, if property not found, Pair with first = false if property is ignored, otherwise Pair: first=true, second property-value
     */
    private fun getPropertyValue(
        obj: Any,
        colDef: ExcelColumnDef,
        identifier: String?,
        ignoreProperties: Array<out String>
    ): PropertySearchResult? {
        identifier ?: return null
        if (ignoreProperties.any { identifier.compareTo(it, ignoreCase = true) == 0 }) {
            return PropertySearchResult(true, null)
        }
        sheet.cache.autoFillCache.foundFieldsMap[colDef]?.let {
            return PropertySearchResult(false, BeanUtils.getValue(obj, it))
        }
        val field = BeanUtils.getDeclaredField(obj::class.java, identifier.decapitalize()) ?: return null
        sheet.cache.autoFillCache.foundFieldsMap[colDef] = field
        return PropertySearchResult(false, BeanUtils.getValue(obj, field))
    }

    var heightInPoints: Float
        get() = row.heightInPoints
        set(height) {
            row.heightInPoints = height
        }

    val lastCellNum: Short
        get() = row.lastCellNum

    private class PropertySearchResult(val propertyIgnored: Boolean, val value: Any?)
}
