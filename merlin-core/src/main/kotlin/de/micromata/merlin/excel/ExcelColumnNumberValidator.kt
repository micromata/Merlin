package de.micromata.merlin.excel

import de.micromata.merlin.utils.Converter
import de.micromata.merlin.word.ConditionalComparator
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

/**
 * Validates Excel cells (must be numbers).
 * @see CellType.NUMERIC
 */
class ExcelColumnNumberValidator @JvmOverloads constructor(
        /**
         * @param minimum If given each number must be equals or higher than this given minimum value. Default is null.
         */
        var minimum: Double? = null,
        /**
         *
         * @param maximum If given each number must be equals or lower than this given maximum value. Default is null.
         */
        var maximum: Double? = null) : ExcelColumnValidator() {

    override fun clone(): ExcelColumnNumberValidator {
        val clone = ExcelColumnNumberValidator()
        clone.copyFrom(this)
        return clone
    }

    override fun copyFrom(src: ExcelColumnValidator) {
        super.copyFrom(src)
        src as ExcelColumnNumberValidator
        this.minimum = src.minimum
        this.maximum = src.maximum
    }

    /**
     * If true a string value of a cell will be converted by this validator to a string (if possible). Default is false.
     * @param tryToConvertStringToNumber The value to set.
     */
    var isTryToConvertStringToNumber = false

    /**
     * Checks if the cell value is of type [CellType.NUMERIC] or if [.isTryToConvertStringToNumber] is true if
     * it is possible to convert a string cell value to a number.
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
        if (PoiHelper.isEmpty(cell)) {
            return null // Do not check empty cells. If required, it's done by super.
        }
        // cell is not null:
        var doubleValue: Double? = null
        if (cell!!.cellType == CellType.NUMERIC) {
            doubleValue = cell.numericCellValue
        } else if (isTryToConvertStringToNumber && cell.cellType == CellType.STRING) {
            doubleValue = Converter.createDouble(cell.stringCellValue)
        }
        if (doubleValue == null) {
            return createValidationError(MESSAGE_NUMBER_EXPECTED, rowNumber, PoiHelper.getValueAsString(cell))
        }
        if (minimum != null && ConditionalComparator.greaterThan(minimum!!, doubleValue)) {
            return createValidationError(MESSAGE_NUMBER_LESS_THAN_MINIMUM, rowNumber, doubleValue, minimum)
        }
        return if (maximum != null && ConditionalComparator.greaterThan(doubleValue, maximum!!)) {
            createValidationError(MESSAGE_NUMBER_GREATER_THAN_MAXIMUM, rowNumber, doubleValue, maximum)
        } else null
    }

    companion object {
        const val MESSAGE_NUMBER_EXPECTED = "merlin.excel.validation_error.number_expected"
        const val MESSAGE_NUMBER_LESS_THAN_MINIMUM = "merlin.excel.validation_error.number_less_than_minimum"
        const val MESSAGE_NUMBER_GREATER_THAN_MAXIMUM = "merlin.excel.validation_error.number_greater_than_maximum"
    }
}
