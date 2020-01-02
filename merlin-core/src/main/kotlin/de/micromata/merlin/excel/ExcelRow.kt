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

    var heightInPoints: Float
        get() = row.heightInPoints
        set(height) {
            row.heightInPoints = height
        }

    val lastCellNum: Short
        get() = row.lastCellNum

}
