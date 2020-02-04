package de.micromata.merlin.excel

import de.micromata.merlin.word.templating.TemplateRunContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.RichTextString
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Optional holder for POI cells. Useful for creating new cells.
 */
class ExcelCell @JvmOverloads internal constructor(private val row: ExcelRow, val cell: Cell, private val type: ExcelCellType? = null, cellStyle: CellStyle? = null) {
    init {
        if (cellStyle != null) {
            cell.cellStyle = cellStyle
        }
    }

    fun setBlank(): ExcelCell {
        cell.setBlank()
        return this
    }

    fun setCellValue(str: String?): ExcelCell {
        cell.setCellValue(str)
        return this
    }

    fun setCellValue(value: Double): ExcelCell {
        cell.setCellValue(value)
        return this
    }

    fun setCellValue(value: Float): ExcelCell {
        cell.setCellValue(value.toDouble())
        return this
    }

    fun setCellValue(value: Int): ExcelCell {
        cell.setCellValue(value.toDouble())
        return this
    }

    fun setCellValue(value: Long): ExcelCell {
        cell.setCellValue(value.toDouble())
        return this
    }

    fun setCellValue(str: RichTextString?): ExcelCell {
        cell.setCellValue(str)
        return this
    }

    fun setCellValue(date: Date): ExcelCell {
        cell.setCellValue(date)
        return this
    }

    fun setCellValue(calendar: Calendar): ExcelCell {
        cell.setCellValue(calendar)
        return this
    }

    fun setCellValue(date: LocalDateTime): ExcelCell {
        cell.setCellValue(date)
        return this
    }

    fun setCellValue(date: LocalDate): ExcelCell {
        cell.setCellValue(date)
        return this
    }

    /**
     * @param workbook Needed for creating int DataFormat.
     * @param intValue The value to set.
     * @return this for chaining.
     */
    fun setCellValue(workbook: ExcelWorkbook, intValue: Int): ExcelCell {
        setCellValue(workbook, cell, intValue)
        return this
    }

    /**
     * @param workbook     Needed for creating int DataFormat.
     * @param booleanValue The value to set.
     * @return this for chaining.
     */
    fun setCellValue(workbook: ExcelWorkbook, booleanValue: Boolean): ExcelCell {
        setCellValue(workbook, cell, booleanValue)
        return this
    }

    /**
     * @param booleanValue The value to set.
     * @return this for chaining.
     */
    fun setCellFormula(formula: String): ExcelCell {
        setCellFormula(cell, formula)
        return this
    }

    fun removeFormula(): ExcelCell {
        cell.removeFormula()
        return this
    }

    /**
     * @param workbook    Needed for creating int DataFormat.
     * @param doubleValue The value to set.
     * @return this for chaining.
     */
    fun setCellValue(workbook: ExcelWorkbook, doubleValue: Double): ExcelCell {
        setCellValue(workbook, cell, doubleValue)
        return this
    }

    fun setCellStyle(style: CellStyle?): ExcelCell {
        cell.cellStyle = style
        return this
    }

    /**
     * @param cellStyleId Id of the cell style for re-usage. If not given, cell style will not saved for re-usage.
     */
    fun cloneCellStyle(cellStyleId: String? = null): CellStyle {
        val cellStyle = row.sheet.excelWorkbook.createOrGetCellStyle(cellStyleId)
        val origCellStyle: CellStyle = this.cell.cellStyle
        if (origCellStyle != null) {
            cellStyle.cloneStyleFrom(origCellStyle)
        }
        return cellStyle
    }

    val stringCellValue: String
        get() = cell.stringCellValue

    val numericCellValue: Double
        get() = cell.numericCellValue

    fun evaluateFormularCell() {
        row.sheet.excelWorkbook.formulaEvaluator!!.evaluateFormulaCell(cell)
    }

    companion object {
        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, doubleValue: Double) {
            cell.setCellValue(doubleValue)
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT)
        }

        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, intValue: Int) {
            cell.setCellValue(intValue.toDouble())
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.INT)
        }

        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, booleanValue: Boolean) {
            cell.setCellValue(TemplateRunContext.getBooleanAsString(booleanValue))
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.INT)
        }

        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, format: String?, dateValue: Date?) {
            cell.setCellValue(dateValue)
            cell.cellStyle = workbook.ensureDateCellStyle(format!!)
        }

        @JvmStatic
        fun setCellFormula(cell: Cell, formula: String) {
            cell.setCellFormula(formula)
        }

        @JvmStatic
        fun copyCell(src: Cell, dest: Cell) {
            dest.cellStyle = src.cellStyle
            when (src.cellType) {
                CellType.BLANK -> dest.setBlank()
                CellType.FORMULA -> dest.cellFormula = src.cellFormula
                CellType.STRING -> dest.setCellValue(src.richStringCellValue)
                CellType.NUMERIC -> dest.setCellValue(src.numericCellValue)
                CellType.BOOLEAN -> dest.setCellValue(src.booleanCellValue)
                CellType.ERROR -> dest.setCellErrorValue(src.errorCellValue)
            }
        }
    }
}
