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
     * @param columnNumber The column number.
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
            }
            excelCell = ensureCell(cell)
        }
        return excelCell
    }

    private fun ensureCell(cell: Cell?): ExcelCell {
        var excelCell = cellMap[cell!!.columnIndex]
        if (excelCell == null) {
            excelCell = ExcelCell(this, cell)
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
            var pair = getPropertyValue(obj, colDef, colDef.columnHeadname, ignoreProperties)
            if (pair != null) {
                pair.second?.let { value ->
                    processPropertyValue(obj, value, colDef, process)
                }
                continue
            }
            for (alias in colDef.columnAliases) {
                pair = getPropertyValue(obj, colDef, alias.decapitalize(), ignoreProperties)
                if (pair != null) {
                    pair.second?.let { value ->
                        processPropertyValue(obj, value, colDef, process)
                    }
                    break
                }
            }
            if (pair == null) {
                // No bean property found for this column definition.
                sheet.cache.autoFillCache.notFoundFieldsSet.add(colDef)
            }
        }
    }

    private fun processPropertyValue(
        obj: Any,
        value: Any,
        colDef: ExcelColumnDef,
        process: (Any, Any, ExcelCell, ExcelColumnDef) -> Boolean
    ) {
        val cell = getCell(colDef)
        if (!process(obj, value, cell, colDef)) {
            cell.setCellValue(value)
        }
    }

    private fun getPropertyValue(
        obj: Any,
        colDef: ExcelColumnDef,
        identifier: String?,
        ignoreProperties: Array<out String>
    ): Pair<Boolean, Any?>? {
        identifier ?: return null
        if (ignoreProperties.any { identifier.compareTo(it, ignoreCase = true) == 0 }) {
            return null
        }
        sheet.cache.autoFillCache.foundFieldsMap[colDef]?.let {
            return Pair(true, BeanUtils.getValue(obj, it))
        }
        val field = BeanUtils.getDeclaredField(obj::class.java, identifier.decapitalize()) ?: return null
        sheet.cache.autoFillCache.foundFieldsMap[colDef] = field
        return Pair(true, BeanUtils.getValue(obj, field))
    }

    var heightInPoints: Float
        get() = row.heightInPoints
        set(height) {
            row.heightInPoints = height
        }

    val lastCellNum: Short
        get() = row.lastCellNum

}
