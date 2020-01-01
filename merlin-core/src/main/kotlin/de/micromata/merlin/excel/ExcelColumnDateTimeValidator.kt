package de.micromata.merlin.excel

import org.apache.poi.ss.usermodel.Cell
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Validates each cell of a column: Each cell must be a valid Excel date format.
 */
class ExcelColumnDateTimeValidator
@JvmOverloads constructor(dateFormats: Array<String> = ENGLISH_MONTH_FIRST_DATETIME_FORMATS,
                          locale: Locale = Locale.getDefault(),
                          minimum: LocalDateTime? = null,
                          maximum: LocalDateTime? = null)
    : AbstractExcelColumnDateValidator<LocalDateTime>(dateFormats, locale, minimum, maximum) {

    override fun clone(): ExcelColumnDateTimeValidator {
        val clone = ExcelColumnDateTimeValidator()
        clone.copyFrom(this)
        return clone
    }

    override fun getDate(cell: Cell?): LocalDateTime? {
        if (cell == null)
            return null
        val date = getLocalDateTimeCellValue(cell)
        if (date != null)
            return date
        return parse(cell, LocalDateTime::parse, "datetime")
    }

    override fun isBefore(d1: LocalDateTime, d2: LocalDateTime): Boolean {
        return d1.isBefore(d2)
    }

    override fun asString(date: LocalDateTime?): String? {
        return if (date == null)
            null
        else
            DateTimeFormatter.ISO_DATE_TIME.format(date)
    }

    companion object {
        @JvmStatic
        val GERMAN_DATETIME_FORMATS = arrayOf("d.M.yyyy H:m[:s]", "d.M.yy H:m[:s]", "d. MMMM yyyy H:m[:s]", "yyyy-MM-dd H:m[:s]")

        @JvmStatic
        val ENGLISH_MONTH_FIRST_DATETIME_FORMATS = arrayOf("M/d/yyyy H:m[:s]", "M/d/yy H:m[:s]", "yyyy-MM-dd H:m[:s]")

        @JvmStatic
        val ENGLISH_DAY_FIRST_DATETIME_FORMATS = arrayOf("M/d/yyyy H:m[:s]", "M/d/yy H:m[:s]", "yyyy-MM-dd H:m[:s]")
    }
}
