package de.micromata.merlin.excel

import de.micromata.merlin.word.templating.TemplateRunContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.RichTextString
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Optional holder for POI cells. Useful for creating new cells.
 */
class ExcelCell @JvmOverloads internal constructor(
    val row: ExcelRow,
    val cell: Cell,
    private val type: ExcelCellType? = null,
    cellStyle: CellStyle? = null
) {
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

    fun setCellValue(value: BigDecimal): ExcelCell {
        cell.setCellValue(value.toDouble())
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
        cell.cellStyle = workbook.ensureStandardCellStyle(date)
        return this
    }

    fun setCellValue(calendar: Calendar): ExcelCell {
        cell.setCellValue(calendar)
        cell.cellStyle = workbook.ensureStandardCellStyle(calendar)
        return this
    }

    fun setCellValue(dateTime: LocalDateTime): ExcelCell {
        cell.setCellValue(dateTime)
        cell.cellStyle = workbook.ensureStandardCellStyle(dateTime)
        return this
    }

    fun setCellValue(date: LocalDate): ExcelCell {
        cell.setCellValue(date)
        cell.cellStyle = workbook.ensureStandardCellStyle(date)
        return this
    }

    fun setCellValue(value: Any?): ExcelCell {
        value?.let {
            when (it) {
                is String -> setCellValue(it)
                is BigDecimal -> setCellValue(it)
                is Double -> setCellValue(it)
                is Float -> setCellValue(it)
                is Int -> setCellValue(it)
                is Long -> setCellValue(it)
                is RichTextString -> setCellValue(it)
                is Date -> setCellValue(it)
                is LocalDate -> setCellValue(it)
                is LocalDateTime -> setCellValue(it)
                is Calendar -> setCellValue(it)
                else -> setCellValue(it.toString())
            }
        } ?: setBlank()
        return this
    }

    /**
     * @param workbook Needed for creating int DataFormat.
     * @param intValue The value to set.
     * @return this for chaining.
     */
    @Deprecated("Use setCellValue(Boolean) instead.")
    fun setCellValue(workbook: ExcelWorkbook, intValue: Int): ExcelCell {
        setCellValue(workbook, cell, intValue)
        return this
    }

    /**
     * @param workbook     Needed for creating int DataFormat.
     * @param booleanValue The value to set.
     * @return this for chaining.
     */
    @Deprecated("Use setCellValue(Boolean) instead.")
    fun setCellValue(workbook: ExcelWorkbook, booleanValue: Boolean): ExcelCell {
        setCellValue(workbook, cell, booleanValue)
        return this
    }

    /**
     * @param formula The value to set.
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
    @Deprecated("Use setCellValue(Boolean) instead.")
    fun setCellValue(workbook: ExcelWorkbook, doubleValue: Double): ExcelCell {
        setCellValue(workbook, cell, doubleValue)
        return this
    }

    /**
     * @param workbook    Needed for creating int DataFormat.
     * @param value The value to set.
     * @return this for chaining.
     */
    @Deprecated("Use setCellValue(Boolean) instead.")
    fun setCellValue(workbook: ExcelWorkbook, value: BigDecimal): ExcelCell {
        setCellValue(workbook, cell, value)
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
        val origCellStyle: CellStyle? = this.cell.cellStyle
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

    val sheet = row.sheet

    val workbook = sheet.excelWorkbook

    companion object {
        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, doubleValue: Double) {
            cell.setCellValue(doubleValue)
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT)
        }

        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, value: BigDecimal) {
            cell.setCellValue(value.toDouble())
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT)
        }

        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, intValue: Int) {
            cell.setCellValue(intValue.toDouble())
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.INT)
        }

        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, format: String, dateValue: Date?) {
            cell.setCellValue(dateValue)
            cell.cellStyle = workbook.ensureDateCellStyle(format)
        }

        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, booleanValue: Boolean) {
            cell.setCellValue(TemplateRunContext.getBooleanAsString(booleanValue))
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.INT)
        }

        @JvmStatic
        fun setCellFormula(cell: Cell, formula: String) {
            cell.cellFormula = formula
        }

        @JvmStatic
        fun copyCell(src: Cell, dest: Cell) {
            dest.cellStyle = src.cellStyle
            when (src.cellType) {
                CellType.BLANK -> dest.setBlank()
                CellType.FORMULA -> dest.cellFormula = src.cellFormula
                CellType.NUMERIC -> dest.setCellValue(src.numericCellValue)
                CellType.BOOLEAN -> dest.setCellValue(src.booleanCellValue)
                CellType.ERROR -> dest.setCellErrorValue(src.errorCellValue)
                else -> dest.setCellValue(src.richStringCellValue)
            }
        }
    }
}
