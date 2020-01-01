package de.micromata.merlin.excel

import org.apache.poi.ss.usermodel.Cell
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Validates each cell of a column: Each cell must be a valid Excel date format.
 */
class ExcelColumnDateValidator
@JvmOverloads constructor(dateFormats: Array<String> = ENGLISH_MONTH_FIRST_FORMATS,
                          locale: Locale = Locale.getDefault(),
                          minimum: LocalDate? = null,
                          maximum: LocalDate? = null)
    : AbstractExcelColumnDateValidator<LocalDate>(dateFormats, locale, minimum, maximum) {

    override fun clone(): ExcelColumnDateValidator {
        val clone = ExcelColumnDateValidator()
        clone.copyFrom(this)
        return clone
    }

    override fun getDate(cell: Cell?): LocalDate? {
        if (cell == null)
            return null
        val date = getLocalDateTimeCellValue(cell)
        if (date != null)
            return date.toLocalDate()
        return parse(cell, LocalDate::parse, "date")
    }

    override fun isBefore(d1: LocalDate, d2: LocalDate): Boolean {
        return d1.isBefore(d2)
    }

    override fun asString(date: LocalDate?): String? {
        return if (date == null)
            null
        else
            DateTimeFormatter.ISO_DATE.format(date)
    }

    companion object {
        @JvmStatic
        val GERMAN_FORMATS = arrayOf("d.M.yyyy", "d.M.yy", "d. MMMM yyyy", "d. MMMM yy", "yyyy-MM-dd")

        @JvmStatic
        val ENGLISH_MONTH_FIRST_FORMATS = arrayOf("M/d/yyyy", "M/d/yy", "yyyy-MM-dd")

        @JvmStatic
        val ENGLISH_DAY_FIRST_FORMATS = arrayOf("d/M/yyyy", "d/M/yy", "yyyy-MM-dd")
    }
}
