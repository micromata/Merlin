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
class ExcelCell internal constructor(
    val row: ExcelRow,
    val cell: Cell,
    private val existingPoiCell: Boolean
) {
    @Suppress("MemberVisibilityCanBePrivate")
    fun setBlank(): ExcelCell {
        cell.setBlank()
        return this
    }

    fun setCellValue(str: String?): ExcelCell {
        if (str == null) {
            cell.setBlank()
        } else {
            cell.setCellValue(str)
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(value: BigDecimal, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        if (protectCellStyle) {
            cell.setCellValue(value.toDouble())
        } else {
            // Set cell value and auto style:
            setCellValue(workbook, cell, value)
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(value: Double, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        if (protectCellStyle) {
            cell.setCellValue(value)
        } else {
            // Set cell value and auto style:
            setCellValue(workbook, cell, value)
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(value: Float, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        if (protectCellStyle) {
            cell.setCellValue(value.toDouble())
        } else {
            // Set cell value and auto style:
            setCellValue(workbook, cell, value.toDouble())
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(value: Int, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        if (protectCellStyle) {
            cell.setCellValue(value.toDouble())
        } else {
            // Set cell value and auto style:
            setCellValue(workbook, cell, value)
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(value: Long, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        if (protectCellStyle) {
            cell.setCellValue(value.toDouble())
        } else {
            // Set cell value and auto style:
            setCellValue(workbook, cell, value)
        }
        return this
    }

    fun setCellValue(str: RichTextString?): ExcelCell {
        if (str == null) {
            setBlank()
        } else {
            cell.setCellValue(str)
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(date: Date, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        cell.setCellValue(date)
        if (!protectCellStyle) {
            cell.cellStyle = workbook.ensureStandardCellStyle(date)
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(calendar: Calendar, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        cell.setCellValue(calendar)
        if (!protectCellStyle) {
            cell.cellStyle = workbook.ensureStandardCellStyle(calendar)
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(dateTime: LocalDateTime, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        cell.setCellValue(dateTime)
        if (!protectCellStyle) {
            cell.cellStyle = workbook.ensureStandardCellStyle(dateTime)
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(date: LocalDate, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        cell.setCellValue(date)
        if (!protectCellStyle) {
            cell.cellStyle = workbook.ensureStandardCellStyle(date)
        }
        return this
    }

    /**
     * @param protectCellStyle If true, the cell style will not be set fot this value type.
     * Default is true for already existing poi cells (if existing excel workbooks are used).
     */
    @JvmOverloads
    fun setCellValue(value: Any?, protectCellStyle: Boolean = existingPoiCell): ExcelCell {
        value?.let {
            when (it) {
                is String -> setCellValue(it)
                is BigDecimal -> setCellValue(it, protectCellStyle)
                is Double -> setCellValue(it, protectCellStyle)
                is Float -> setCellValue(it, protectCellStyle)
                is Int -> setCellValue(it, protectCellStyle)
                is Long -> setCellValue(it, protectCellStyle)
                is RichTextString -> setCellValue(it)
                is Date -> setCellValue(it, protectCellStyle)
                is LocalDate -> setCellValue(it, protectCellStyle)
                is LocalDateTime -> setCellValue(it, protectCellStyle)
                is Calendar -> setCellValue(it, protectCellStyle)
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
    @Suppress("unused")
    fun setCellFormula(formula: String): ExcelCell {
        setCellFormula(cell, formula)
        return this
    }

    @Suppress("unused")
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
    @Suppress("unused")
    fun cloneCellStyle(cellStyleId: String? = null): CellStyle {
        val cellStyle = row.sheet.excelWorkbook.createOrGetCellStyle(cellStyleId)
        val origCellStyle: CellStyle? = this.cell.cellStyle
        if (origCellStyle != null) {
            cellStyle.cloneStyleFrom(origCellStyle)
        }
        return cellStyle
    }

    val stringCellValue: String?
        get() = PoiHelper.getValueAsString(cell)

    /**
     * Throws exception if the celltype contains no numeric value.
     */
    val numericCellValue: Double
        get() = cell.numericCellValue

    val intCellValue: Int
        get() = cell.numericCellValue.toInt()

    /**
     * @param localDateTime If true, any dates will be returned as [LocalDateTime], otherwise as [java.util.Date]
     */
    @JvmOverloads
    fun getValue(localDateTime: Boolean = true): Any? {
        return PoiHelper.getValue(cell, localDateTime)
    }

    /**
     * @param locale Used for number format.
     * @param trimValue If true, the result string will be trimmed. Default is false.
     * @see PoiHelper.getValueAsString
     */
    @JvmOverloads
    fun getValueAsString(locale: Locale = Locale.getDefault(), trimValue: Boolean = false): String? {
        return PoiHelper.getValueAsString(cell, locale, trimValue)
    }

    @Suppress("unused")
    fun evaluateFormularCell() {
        row.sheet.excelWorkbook.formulaEvaluator!!.evaluateFormulaCell(cell)
    }

    val sheet = row.sheet

    val workbook = sheet.excelWorkbook

    companion object {
        /**
         * Sets automatically the cell style.
         */
        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, doubleValue: Double) {
            cell.setCellValue(doubleValue)
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT)
        }

        /**
         * Sets automatically the cell style.
         */
        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, value: BigDecimal) {
            cell.setCellValue(value.toDouble())
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT)
        }

        /**
         * Sets automatically the cell style.
         */
        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, intValue: Int) {
            cell.setCellValue(intValue.toDouble())
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.INT)
        }

        /**
         * Sets automatically the cell style.
         */
        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, longValue: Long) {
            cell.setCellValue(longValue.toDouble())
            cell.cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.INT)
        }

        /**
         * Sets automatically the cell style.
         */
        @JvmStatic
        fun setCellValue(workbook: ExcelWorkbook, cell: Cell, format: String, dateValue: Date?) {
            cell.setCellValue(dateValue)
            cell.cellStyle = workbook.ensureDateCellStyle(format)
        }

        /**
         * Sets automatically the cell style.
         */
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
