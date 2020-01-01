package de.micromata.merlin.excel

import de.micromata.merlin.excel.PoiHelper.getValueAsString
import de.micromata.merlin.excel.PoiHelper.isEmpty
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

/**
 * Validates each cell of a column: Each cell must be a valid Excel date format.
 */
class ExcelColumnDateValidator(vararg dateFormats: String) : ExcelColumnValidator() {
    private lateinit var dateTimeFormatters: List<DateTimeFormatter>
        private set
    var dateFormats: Array<out String>
        private set

    /**
     * Locale for parsing month names, such as July, 16
     */
    var locale = Locale.getDefault()
        set(value) {
            field = value
            initDateFormaters()
        }

    init {
        this.dateFormats = dateFormats
        initDateFormaters()
    }

    private fun initDateFormaters() {
        val formatters = mutableListOf<DateTimeFormatter>()
        dateFormats.forEach {
            formatters.add(DateTimeFormatter.ofPattern(it).withLocale(locale))
        }
        this.dateTimeFormatters = formatters
    }

    override fun copyFrom(src: ExcelColumnValidator) {
        super.copyFrom(src)
        src as ExcelColumnDateValidator
        this.dateFormats = src.dateFormats
        initDateFormaters()
    }

    /**
     * Representation without time zone information.
     */
    fun getLocalDateTime(cell: Cell?): LocalDateTime? {
        if (cell == null)
            return null
        val date = getLocalDateTimeCellValue(cell)
        if (date != null)
            return date
        return parse(cell, LocalDateTime::parse, "dateTime")
    }

    /**
     * Representation without time zone information.
     */
    @Suppress("unused")
    fun getLocalDate(cell: Cell?): LocalDate? {
        if (cell == null)
            return null
        val date = getLocalDateTimeCellValue(cell)
        if (date != null)
            return date.toLocalDate()
        return parse(cell, LocalDate::parse, "date")
    }

    private fun getLocalDateTimeCellValue(cell: Cell): LocalDateTime? {
        if (cell.cellType != CellType.NUMERIC) {
            return null
        }
        try {
            return cell.localDateTimeCellValue
        } catch (ex: Exception) {
            if (log.isDebugEnabled) {
                log.debug(ex.message, ex)
            }
        }
        return null
    }

    private fun <T> parse(cell: Cell, parse: (String, DateTimeFormatter) -> T, type: String): T? {
        if (cell.cellType == CellType.STRING) {
            val strVal = PoiHelper.getValueAsString(cell, true)
            if (strVal.isNullOrBlank()) {
                return null
            }
            this.dateTimeFormatters.forEachIndexed { index, formatter ->
                try {
                    return parse(strVal, formatter)
                } catch (ex: DateTimeParseException) {
                    // Date doesn't fit this format.
                    if (log.isDebugEnabled)
                        log.debug("Couldn't parse '$strVal' ($type) with pattern '${dateFormats[index]}': ${ex.message}. $formatter, locale=${formatter.locale}")
                    // println("Couldn't parse '$strVal' ($type) with pattern '${dateFormats[index]}': ${ex.message}. $formatter, locale=${formatter.locale}")
                }
            }
        }
        return null
    }

    /**
     * Checks if the cell value is date formatted.
     *
     * @param cell The cell to validate.
     * @param rowNumber Row number of cell value in given sheet.
     * @return null if valid, otherwise validation error message to display.
     */
    override fun isValid(cell: Cell?, rowNumber: Int): ExcelValidationErrorMessage? {
        val errorMessage = super.isValid(cell, rowNumber)
        if (errorMessage != null) {
            return errorMessage
        }
        if (isEmpty(cell)) {
            return null // Do not check empty cells. If required, it's done by super.
        }
        var isDateFormatted = false
        // cell isn't null
        if (cell!!.cellType == CellType.NUMERIC) {
            try {
                isDateFormatted = DateUtil.isCellDateFormatted(cell)
            } catch (ex: IllegalStateException) {
                if (log.isDebugEnabled) {
                    log.debug(ex.message, ex)
                }
            }
        } else if (cell.cellType == CellType.STRING) {
            isDateFormatted = getLocalDateTime(cell) != null
        }
        return if (!isDateFormatted) {
            createValidationError(MESSAGE_DATE_EXPECTED, rowNumber, getValueAsString(cell))
        } else null
    }

    companion object {
        /**
         * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number,
         * Cell value, pattern
         */
        const val MESSAGE_DATE_EXPECTED = "merlin.excel.validation_error.date_expected"
        private val log = LoggerFactory.getLogger(ExcelColumnDateValidator::class.java)

        @JvmStatic
        val GERMAN_DATE_FORMATS = arrayOf("d.M.yyyy", "d.M.yy", "d. MMMM yyyy", "d. MMMM yy", "yyyy-MM-dd")

        @JvmStatic
        val GERMAN_DATETIME_FORMATS = arrayOf("d.M.yyyy H:m[:s]", "d.M.yy H:m[:s]", "d. MMMM yyyy H:m[:s]", "yyyy-MM-dd H:m[:s]")

        @JvmStatic
        val ENGLISH_MONTH_FIRST_DATE_FORMATS = arrayOf("M/d/yyyy", "M/d/yy", "yyyy-MM-dd")

        @JvmStatic
        val ENGLISH_DAY_FIRST_DATE_FORMATS = arrayOf("d/M/yyyy", "d/M/yy", "yyyy-MM-dd")
    }
}
