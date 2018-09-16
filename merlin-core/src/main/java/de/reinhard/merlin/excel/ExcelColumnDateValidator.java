package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates each cell of a column: Each cell must be a valid Excel date format.
 */
public class ExcelColumnDateValidator extends ExcelColumnValidator {
    /**
     * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number,
     * Cell value, pattern
     */
    public static final String MESSAGE_DATE_EXPECTED = "merlin.excel.validation_error.date_expected";

    private Logger log = LoggerFactory.getLogger(ExcelColumnDateValidator.class);

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
        boolean isDateFormatted = false;
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            try {
                isDateFormatted = DateUtil.isCellDateFormatted(cell);
            } catch (IllegalStateException ex) {
                if (log.isDebugEnabled()) {
                    log.debug(ex.getMessage(), ex);
                }
            }
        }
        if (!isDateFormatted) {
            return createValidationError(MESSAGE_DATE_EXPECTED, rowNumber, PoiHelper.getValueAsString(cell));
        }
        return null;
    }
}

