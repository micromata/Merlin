package de.reinhard.merlin.excel;

import de.reinhard.merlin.utils.Converter;
import de.reinhard.merlin.word.ConditionalComparator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelColumnNumberValidator extends ExcelColumnValidator {
    public static final String MESSAGE_NUMBER_EXPECTED = "merlin.excel.validation_error.number_expected";

    public static final String MESSAGE_NUMBER_LESS_THAN_MINIMUM = "merlin.excel.validation_error.number_less_than_minimum";

    public static final String MESSAGE_NUMBER_GREATER_THAN_MAXIMUM = "merlin.excel.validation_error.number_greater_than_maximum";

    private Logger log = LoggerFactory.getLogger(ExcelColumnNumberValidator.class);

    private Double minimum, maximum;

    /**
     * Checks if the cell value is date formatted.
     *
     * @param cell
     * @param rowNumber Row number of cell value in given sheet.
     * @return null if valid, otherwise validation error message to display.
     */
    @Override
    public ExcelValidationErrorMessage isValid(Cell cell, int rowNumber) {
        ExcelValidationErrorMessage errorMessage = super.isValid(cell, rowNumber);
        if (errorMessage != null) {
            return errorMessage;
        }
        if (PoiHelper.isEmpty(cell)) {
            return null; // Do not check empty cells. If required, it's done by super.
        }
        Double val = null;
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            val = cell.getNumericCellValue();
        } else if (cell.getCellTypeEnum() == CellType.STRING) {
            val = Converter.createDouble(cell.getStringCellValue());
        }
        if (val == null) {
            return createValidationError(MESSAGE_NUMBER_EXPECTED, rowNumber, PoiHelper.getValueAsString(cell));
        }
        if (minimum != null && ConditionalComparator.greaterThan(minimum, val)) {
            return createValidationError(MESSAGE_NUMBER_LESS_THAN_MINIMUM, rowNumber, val, minimum);
        }
        if (maximum != null && ConditionalComparator.greaterThan(val, maximum)) {
            return createValidationError(MESSAGE_NUMBER_GREATER_THAN_MAXIMUM, rowNumber, val, maximum);
        }
        return null;
    }

    public Double getMinimum() {
        return minimum;
    }

    public void setMinimum(Double minimum) {
        this.minimum = minimum;
    }

    public Double getMaximum() {
        return maximum;
    }

    public void setMaximum(Double maximum) {
        this.maximum = maximum;
    }
}

