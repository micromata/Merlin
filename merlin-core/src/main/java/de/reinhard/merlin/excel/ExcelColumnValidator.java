package de.reinhard.merlin.excel;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.ResultMessageStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ExcelColumnValidator extends ExcelColumnListener {
    /**
     * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number
     */
    public static final String MESSAGE_MISSING_REQUIRED_FIELD = "merlin.excel.validation_error.missing_required_field";
    /**
     * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number,
     * Cell value, row of first occurrence.
     */
    public static final String MESSAGE_VALUE_NOT_UNIQUE = "merlin.excel.validation_error.value_not_unique";

    private Logger log = LoggerFactory.getLogger(ExcelColumnValidator.class);
    private boolean required;
    private boolean unique;
    private boolean columnHeadnameFound;
    // Used for unique constraint.
    private Set<String> entries = new TreeSet<>();
    private Map<String, Integer> cellValueMap;
    private Set<ExcelValidationErrorMessage> validationErrors;

    /**
     * Overwrite this for own validation.
     * Checks required and unique if configured, otherwise returns null.
     *
     * @param cell
     * @param rowNumber Row number of cell value in given sheet.
     * @return null if valid, otherwise validation error message to display.
     */
    public ExcelValidationErrorMessage isValid(Cell cell, int rowNumber) {
        if (PoiHelper.isEmpty(cell)) {
            if (required) {
                return createValidationErrorRequired(rowNumber);
            }
            return null;
        }
        String cellValue = PoiHelper.getValueAsString(cell);
        Integer firstOccurrenceRowNumber = isUnique(cellValue, rowNumber);
        if (firstOccurrenceRowNumber != null && firstOccurrenceRowNumber != rowNumber) {
            return createValidationErrorUnique(rowNumber, cellValue, firstOccurrenceRowNumber);
        }
        return null;
    }

    @Override
    public void readCell(Cell cell, int rowNumber) {
        ExcelValidationErrorMessage resultMessage = isValid(cell, rowNumber);
        if (resultMessage != null) {
            if (log.isDebugEnabled()) {
                log.debug("Validation error found: " + resultMessage.getMessageWithAllDetails(I18n.getDefault()));
            }
            getValidationErrors().add(resultMessage);
        }
        String cellValue = PoiHelper.getValueAsString(cell);
        if (isUnique(cellValue, rowNumber) == null) {
            if (cellValueMap == null) {
                cellValueMap = new HashMap<>();
            }
            cellValueMap.put(cellValue, rowNumber);
        }
    }

    public boolean hasValidationErrors() {
        return validationErrors != null;
    }

    public Set<ExcelValidationErrorMessage> getValidationErrors() {
        if (validationErrors == null) {
            validationErrors = new TreeSet<>();
        }
        return validationErrors;
    }

    private Integer isUnique(String cellValue, int rowNumber) {
        if (!isUnique()) {
            return null;
        }
        if (cellValueMap == null) {
            cellValueMap = new HashMap<>();
        }
        return cellValueMap.get(cellValue);
    }

    public String getColumnHeadname() {
        return columnDef.getColumnHeadname();
    }

    public ExcelColumnDef getColumnDef() {
        return columnDef;
    }

    public boolean isRequired() {
        return required;
    }

    /**
     * Mark this column and are all its cell values as required.
     *
     * @return this for chaining.
     */
    public ExcelColumnValidator setRequired() {
        this.required = true;
        return this;
    }

    /**
     * Mark this column and are all its cell values as required.
     *
     * @param required
     * @return this for chaining.
     */
    public ExcelColumnValidator setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public boolean isUnique() {
        return unique;
    }

    /**
     * @return true, if sheet parser found col with specified column header.
     */
    public boolean isColumnHeadnameFound() {
        return columnHeadnameFound;
    }

    /**
     * All cell values must be unique, if given.
     *
     * @return this for chaining.
     */
    public ExcelColumnValidator setUnique() {
        this.unique = true;
        return this;
    }

    /**
     * All cell values must be unique, if given.
     *
     * @param unique
     * @return this for chaining.
     */
    public ExcelColumnValidator setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    ExcelValidationErrorMessage createValidationErrorRequired(int rowNumber) {
        return createValidationError(MESSAGE_MISSING_REQUIRED_FIELD, rowNumber, "");
    }

    ExcelValidationErrorMessage createValidationErrorUnique(int rowNumber, Object cellValue, int firstOccurrenceRowNumber) {
        return createValidationError(MESSAGE_VALUE_NOT_UNIQUE, rowNumber, cellValue, firstOccurrenceRowNumber + 1);
    }

    protected ExcelValidationErrorMessage createValidationError(String messageId, int rowNumber, Object cellValue, Object... params) {
        return new ExcelValidationErrorMessage(messageId, ResultMessageStatus.ERROR, params)
                .setSheet(getSheet())
                .setCellValue(cellValue)
                .setColumnDef(getColumnDef())
                .setRow(rowNumber);
    }
}
