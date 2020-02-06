package de.micromata.merlin.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellRangeAddress
import java.util.*

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
    fun getCell(columnDef: ExcelColumnDef, type: ExcelCellType? = null): ExcelCell? {
        return getCell(columnDef.columnNumber, type)
    }

    /**
     * @param columnNumber The column number.
     * @param type         Only used, if new cell will be created.
     * @return The (created) cell, not null.
     */
    @JvmOverloads
    fun getCell(columnNumber: Int, type: ExcelCellType? = null): ExcelCell? {
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
        return excelCell
    }

    /**
     * Assumes [ExcelCellType.STRING]
     *
     * @return The created cell.
     */
    @JvmOverloads
    fun createCell(type: ExcelCellType? = ExcelCellType.STRING): ExcelCell? {
        var colCount = row.lastCellNum.toInt()
        if (colCount < 0) {
            colCount = 0
        }
        return ensureCell(colCount, type)
    }

    private fun ensureCell(columnIndex: Int, type: ExcelCellType?): ExcelCell? {
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
            cell!!.setCellValue(cellString)
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

    var heightInPoints: Float
        get() = row.heightInPoints
        set(height) {
            row.heightInPoints = height
        }

    val lastCellNum: Short
        get() = row.lastCellNum

}
