package de.reinhard.merlin.excel;

import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.ResultMessageStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.PatternSyntaxException;

public class ColumnValidator extends ColumnListener {
    /**
     * Parameter: rowNumber, ExcelColumnDef
     */
    public static final String MESSAGE_MISSING_REQUIRED_FIELD = "merlin.excel.validation_error.missing_required_field";
    /**
     * Parameter: rowNumber, ExcelColumnDef, cellValue
     */
    public static final String MESSAGE_PATTERN_MISMATCH = "merlin.excel.validation_error.pattern_mismatch";
    /**
     * Parameter: rowNumber, firstOccurrenceRowNumber, ExcelColumnDef, cellValue
     */
    public static final String MESSAGE_VALUE_NOT_UNIQUE = "merlin.excel.validation_error.value_not_unique";

    private Logger log = LoggerFactory.getLogger(ColumnValidator.class);
    private boolean required;
    private boolean unique;
    private String patternRegExp;
    private boolean columnHeadnameFound;
    // Used for unique constraint.
    private Set<String> entries = new TreeSet<>();
    private Map<String, Integer> cellValueMap;
    private List<ExcelValidationErrorMessage> validationErrors;

    /**
     * Overwrite this for own validation.
     * Checks required and pattern match if {@link #patternRegExp} is given, otherwise returns null.
     *
     * @param cellValue
     * @param rowNumber Row number of cell value in given sheet.
     * @return null if valid, otherwise validation error message to display.
     */
    public ExcelValidationErrorMessage isValid(String cellValue, int rowNumber) {
        if (StringUtils.isEmpty(cellValue)) {
            if (required) {
                return createValidationErrorRequired(rowNumber);
            }
            return null;
        }
        if (patternRegExp != null) {
            try {
                if (!cellValue.matches(patternRegExp)) {
                    return createValidationErrorPatternMismatch(rowNumber, cellValue, patternRegExp);
                }
            } catch (PatternSyntaxException ex) {
                log.error("Pattern syntax error for regex for column '" + columnDef.getColumnHeadname() + "': '" + patternRegExp
                        + "': " + ex.getMessage(), ex);
                return null;
            }
        }
        Integer firstOccurrenceRowNumber = isUnique(cellValue, rowNumber);
        if (firstOccurrenceRowNumber != null) {
            return createValidationErrorUnique(rowNumber, cellValue, firstOccurrenceRowNumber);
        }
        return null;
    }

    @Override
    public void readCell(Cell cell, int rowNumber) {
        String cellValue = PoiHelper.getValueAsString(cell);
        ExcelValidationErrorMessage resultMessage = isValid(cellValue, rowNumber);
        if (resultMessage != null) {
            log.debug("Validation error found: " + resultMessage.getMessage());
            getValidationErrors().add(resultMessage);
        }
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

    public List<ExcelValidationErrorMessage> getValidationErrors() {
        if (validationErrors == null) {
            validationErrors = new LinkedList<>();
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
        Integer firstOccurrenceRowNumber = cellValueMap.get(cellValue);
        return firstOccurrenceRowNumber;
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
    public ColumnValidator setRequired() {
        this.required = true;
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
    public ColumnValidator setUnique() {
        this.unique = true;
        return this;
    }

    public String getPatternRegExp() {
        return patternRegExp;
    }

    /**
     * @param patternRegExp String patternRegExp to validate. If null, no patternRegExp match will be validated.
     * @see String#matches(String)
     */
    public void setPatternRegExp(String patternRegExp) {
        this.patternRegExp = patternRegExp;
    }

    ExcelValidationErrorMessage createValidationErrorRequired(int rowNumber) {
        return createValidationError(MESSAGE_MISSING_REQUIRED_FIELD, rowNumber, "");
    }

    ExcelValidationErrorMessage createValidationErrorUnique(int rowNumber, Object cellValue, int firstOccurrenceRowNumber) {
        return createValidationError(MESSAGE_VALUE_NOT_UNIQUE, rowNumber, cellValue, firstOccurrenceRowNumber + 1);
    }

    ExcelValidationErrorMessage createValidationErrorPatternMismatch(int rowNumber, Object cellValue, String patternRegExp) {
        return createValidationError(MESSAGE_PATTERN_MISMATCH, rowNumber, cellValue, patternRegExp);
    }

    private ExcelValidationErrorMessage createValidationError(String messageId, int rowNumber, Object cellValue, Object... params) {
        return new ExcelValidationErrorMessage(messageId, ResultMessageStatus.ERROR, params)
                .setSheet(getSheet())
                .setCellValue(cellValue)
                .setColumnDef(getColumnDef())
                .setRow(rowNumber);
    }
}
