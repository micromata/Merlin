package de.micromata.merlin.excel

import de.micromata.merlin.excel.PoiHelper.getValueAsString
import de.micromata.merlin.excel.PoiHelper.isEmpty
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

/**
 * Validates each cell of a column: Each cell must be a valid Excel date format.
 */
abstract class AbstractExcelColumnDateValidator<T>
@JvmOverloads constructor(dateFormats: Array<String>,
                          locale: Locale,
                          minimum: T? = null,
                          maximum: T? = null)
    : ExcelColumnValidator() {

    private lateinit var dateTimeFormatters: List<DateTimeFormatter>
        private set
    var dateFormats: Array<out String>
        private set

    /**
     * Locale for parsing month names, such as July, 16
     */
    var locale = locale
        set(value) {
            field = value
            initDateFormaters()
        }

    /**
     * @param minimum If given each number must be equals or higher than this given minimum value. Default is null.
     */
    var minimum: T? = minimum
    /**
     *
     * @param maximum If given each number must be equals or lower than this given maximum value. Default is null.
     */
    var maximum: T? = maximum

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
        src as AbstractExcelColumnDateValidator<T>
        this.dateFormats = src.dateFormats
        initDateFormaters()
        this.minimum = src.minimum
        this.maximum = src.maximum
    }

    abstract fun getDate(cell: Cell?): T?

    protected fun <T> parse(cell: Cell, parse: (String, DateTimeFormatter) -> T, type: String): T? {
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

    protected fun getLocalDateTimeCellValue(cell: Cell): LocalDateTime? {
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
        val date = getDate(cell)
        if (date == null) {
            return createValidationError(MESSAGE_DATE_EXPECTED, rowNumber, getValueAsString(cell))
        }
        val min = minimum
        if (min != null && isBefore(date, min)) {
            return createValidationError(ExcelColumnNumberValidator.MESSAGE_NUMBER_LESS_THAN_MINIMUM, rowNumber, asString(date), asString(minimum))
        }
        val max = maximum
        return if (max != null && isBefore(max, date)) {
            createValidationError(ExcelColumnNumberValidator.MESSAGE_NUMBER_GREATER_THAN_MAXIMUM, rowNumber, asString(date), asString(maximum))
        } else null
    }

    /**
     * @return true if d1 is before d2.
     */
    abstract fun isBefore(d1: T, d2: T): Boolean

    /**
     * Formatting the given date as ISO date string (without time zone).
     */
    abstract fun asString(date: T?): String?

    companion object {
        /**
         * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number,
         * Cell value, pattern
         */
        const val MESSAGE_DATE_EXPECTED = "merlin.excel.validation_error.date_expected"
        private val log = LoggerFactory.getLogger(AbstractExcelColumnDateValidator::class.java)
    }
}
