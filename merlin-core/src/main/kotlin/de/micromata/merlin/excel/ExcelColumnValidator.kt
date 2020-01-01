package de.micromata.merlin.excel

import de.micromata.merlin.CoreI18n
import de.micromata.merlin.ResultMessageStatus
import de.micromata.merlin.excel.PoiHelper.getValueAsString
import de.micromata.merlin.excel.PoiHelper.isEmpty
import org.apache.poi.ss.usermodel.Cell
import org.slf4j.LoggerFactory

open class ExcelColumnValidator
@JvmOverloads constructor(required: Boolean = false,
                          unique: Boolean = false)
    : ExcelColumnListener() {
    var isRequired = required
        private set
    var isUnique = unique
        private set

    // Used for unique constraint.
    private var cellValueMap = mutableMapOf<String, Int>()
    val validationErrors = mutableSetOf<ExcelValidationErrorMessage>()

    protected var i18n: CoreI18n = CoreI18n.getDefault()

    override fun clone(): ExcelColumnValidator {
        val clone = ExcelColumnValidator()
        clone.copyFrom(this)
        return clone
    }

    protected open fun copyFrom(src: ExcelColumnValidator) {
        this.isRequired = src.isRequired
        this.isUnique = src.isUnique
        this.i18n = src.i18n
    }

    /**
     * Overwrite this for own validation.
     * Checks required and unique if configured, otherwise returns null.
     *
     * @param cell The cell to validate.
     * @param rowNumber Row number of cell value in given sheet.
     * @return null if valid, otherwise validation error message to display.
     */
    open fun isValid(cell: Cell?, rowNumber: Int): ExcelValidationErrorMessage? {
        if (isEmpty(cell)) {
            return if (isRequired) {
                createValidationErrorRequired(rowNumber)
            } else null
        }
        val cellValue = getValueAsString(cell)
        val firstOccurrenceRowNumber = isUnique(cellValue)
        return if (firstOccurrenceRowNumber != null && firstOccurrenceRowNumber != rowNumber) {
            createValidationErrorUnique(rowNumber, cellValue, firstOccurrenceRowNumber)
        } else null
    }

    override fun readCell(cell: Cell?, rowNumber: Int) {
        val resultMessage = isValid(cell, rowNumber)
        if (resultMessage != null) {
            if (log.isDebugEnabled) {
                log.debug("Validation error found: " + resultMessage.getMessageWithAllDetails(i18n))
            }
            validationErrors.add(resultMessage)
        }
        val cellValue = getValueAsString(cell)
        if (cellValue.isNullOrEmpty()) {
            return
        }
        if (isUnique(cellValue) == null) {
            cellValueMap[cellValue] = rowNumber
        }
    }

    fun hasValidationErrors(): Boolean {
        return validationErrors.isNotEmpty()
    }

    private fun isUnique(cellValue: String?): Int? {
        if (!isUnique) {
            return null
        }
        return cellValueMap[cellValue]
    }

    val columnHeadname: String
        get() = columnDef?.columnHeadname ?: ""

    /**
     * Mark this column and are all its cell values as required.
     *
     * @return this for chaining.
     */
    @JvmOverloads
    fun setRequired(required: Boolean = true): ExcelColumnValidator {
        isRequired = required
        return this
    }

    /**
     * All cell values must be unique, if given.
     *
     * @return this for chaining.
     */
    @JvmOverloads
    fun setUnique(unique: Boolean = true): ExcelColumnValidator {
        isUnique = unique
        return this
    }

    fun createValidationErrorRequired(rowNumber: Int): ExcelValidationErrorMessage {
        return createValidationError(MESSAGE_MISSING_REQUIRED_FIELD, rowNumber, "")
    }

    fun createValidationErrorUnique(rowNumber: Int, cellValue: Any?, firstOccurrenceRowNumber: Int): ExcelValidationErrorMessage {
        return createValidationError(MESSAGE_VALUE_NOT_UNIQUE, rowNumber, cellValue, firstOccurrenceRowNumber + 1)
    }

    protected fun createValidationError(messageId: String, rowNumber: Int, cellValue: Any?, vararg params: Any?): ExcelValidationErrorMessage {
        return ExcelValidationErrorMessage(messageId, ResultMessageStatus.ERROR, *params)
                .setCellValue(cellValue)
                .setColumnDef(columnDef)
                .setRow(rowNumber)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ExcelColumnValidator::class.java)
        /**
         * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number
         */
        const val MESSAGE_MISSING_REQUIRED_FIELD = "merlin.excel.validation_error.missing_required_field"
        /**
         * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number,
         * Cell value, row of first occurrence.
         */
        const val MESSAGE_VALUE_NOT_UNIQUE = "merlin.excel.validation_error.value_not_unique"
    }
}
