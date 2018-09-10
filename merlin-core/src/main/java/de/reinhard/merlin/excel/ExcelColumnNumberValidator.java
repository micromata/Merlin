package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelColumnNumberValidator extends ExcelColumnValidator {
    /**
     * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number,
     * Cell value, pattern
     */
    public static final String MESSAGE_NUMBER_EXPECTED = "merlin.excel.validation_error.number_expected";

    private Logger log = LoggerFactory.getLogger(ExcelColumnNumberValidator.class);

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
        boolean isNumberFormatted = false;
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            isNumberFormatted = true;
        }
        if (!isNumberFormatted) {
            return createValidationError(MESSAGE_NUMBER_EXPECTED, rowNumber, PoiHelper.getValueAsString(cell));
        }
        return null;
    }
}

