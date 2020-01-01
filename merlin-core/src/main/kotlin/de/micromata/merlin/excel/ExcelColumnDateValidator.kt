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

/**
 * Validates each cell of a column: Each cell must be a valid Excel date format.
 */
class ExcelColumnDateValidator(vararg dateFormats: String) : ExcelColumnValidator() {
    val dateTimeFormatters = mutableListOf<DateTimeFormatter>()
    var dateFormats = dateFormats

    init {
        dateFormats.forEach {
            this.dateTimeFormatters.add(DateTimeFormatter.ofPattern(it))//.withZone(zoneId))
        }
    }

    override fun copyFrom(src: ExcelColumnValidator) {
        super.copyFrom(src)
        src as ExcelColumnDateValidator
        this.dateFormats = src.dateFormats
    }

    /**
     * Representation without time zone information.
     */
    fun getLocalDateTime(cell: Cell?): LocalDateTime? {
        if (cell == null) {
            return null
        }
        if (cell.cellType == CellType.NUMERIC) {
            try {
                return cell.localDateTimeCellValue
            } catch (ex: Exception) {
                if (log.isDebugEnabled) {
                    log.debug(ex.message, ex)
                }
            }
        } else if (cell.cellType == CellType.STRING) {
            val strVal = PoiHelper.getValueAsString(cell, true)
            if (strVal.isNullOrBlank()) {
                return null
            }
            this.dateTimeFormatters.forEach {
                try {
                    return LocalDateTime.parse(strVal, it)
                } catch (ex: DateTimeParseException) {
                    // Date doesn't fit this format.
                }
            }
        }
        return null
    }

    /**
     * Representation without time zone information.
     */
    @Suppress("unused")
    fun getLocalDate(cell: Cell?): LocalDate? {
        val date = getLocalDateTime(cell) ?: return null
        return date.toLocalDate()
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
    }
}
