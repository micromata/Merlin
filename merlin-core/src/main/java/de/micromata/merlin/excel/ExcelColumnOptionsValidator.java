package de.micromata.merlin.excel;

import de.micromata.merlin.ResultMessageStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Checks, if a cell matches given options, e. g. colors (red, blue, yellow).
 */
public class ExcelColumnOptionsValidator extends ExcelColumnValidator {
    /**
     * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number,
     * Cell value, pattern
     */
    public static final String MESSAGE_OPTION_MISMATCH = "merlin.excel.validation_error.option_mismatch";

    private Logger log = LoggerFactory.getLogger(ExcelColumnOptionsValidator.class);
    private String[] options;
    private String validationErrorMessageId;
    private boolean caseSensitive;

    /**
     * Cell values must match one of the given options.
     *
     * @param options
     */
    public ExcelColumnOptionsValidator(String... options) {
        this.options = options;
    }

    /**
     * Cell values must match one of the given options.
     *
     * @param options
     */
    public ExcelColumnOptionsValidator(List<Object> options) {
        this.options = options.toArray(new String[0]);
    }

    /**
     * Checks pattern match if the cell value does match any of the configured options, otherwise returns null.
     *
     * @param cell
     * @param rowNumber Row number of cell value in given sheet.
     * @return null if valid, otherwise validation error message to display.
     */
    @Override
    public ExcelValidationErrorMessage isValid(Cell cell, int rowNumber) {
        ExcelValidationErrorMessage errorMessage = super.isValid(cell, rowNumber);
        String cellValue = PoiHelper.getValueAsString(cell);
        if (errorMessage != null) {
            return errorMessage;
        }
        if (PoiHelper.isEmpty(cell)) {
            return null; // Do not check empty cells. If required, it's done by super.
        }
        if (options != null) {
            String val;
            if (caseSensitive) {
                val = cellValue.trim();
            } else {
                val = cellValue.trim().toLowerCase();
            }
            for (String option : options) {
                if (caseSensitive) {
                    if (option.trim().equals(val)) {
                        return null;
                    }
                } else {
                    if (option.trim().toLowerCase().equals(val)) {
                        return null;
                    }
                }
            }
            return new ExcelValidationErrorMessage("merlin.excel.validation_error.options_mismatch", ResultMessageStatus.ERROR,
                    StringUtils.join(options, "; "))
                    .setSheet(getSheet())
                    .setCellValue(cellValue)
                    .setColumnDef(getColumnDef())
                    .setRow(rowNumber);
        }
        return null;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
}

