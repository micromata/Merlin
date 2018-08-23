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
    public static final String MESSAGE_MISSING_REQUIRED_FIELD = ColumnValidator.class.getName() + ":MESSAGE_MISSING_REQUIRED_FIELD";
    /**
     * Parameter: rowNumber, ExcelColumnDef, cellValue
     */
    public static final String MESSAGE_PATTERN_MISMATCH = ColumnValidator.class.getName() + ":MESSAGE_PATTERN_MISMATCH";
    /**
     * Parameter: rowNumber, firstOccurrenceRowNumber, ExcelColumnDef, cellValue
     */
    public static final String MESSAGE_VALUE_NOT_UNIQUE = ColumnValidator.class.getName() + ":MESSAGE_VALUE_NOT_UNIQUE";
    private Logger log = LoggerFactory.getLogger(ColumnValidator.class);
    private boolean required;
    private boolean unique;
    private String patternRegExp;
    private boolean columnHeadnameFound;
    // Used for unique constraint.
    private Set<String> entries = new TreeSet<>();
    private Map<String, Integer> cellValueMap;
    private List<ResultMessage> validationErrors;

    /**
     * Overwrite this for own validation.
     * Checks required and pattern match if {@link #patternRegExp} is given, otherwise returns null.
     *
     * @param cellValue
     * @param rowNumber Row number of cell value in given sheet.
     * @return null if valid, otherwise validation error message to display.
     */
    public ResultMessage isValid(String cellValue, int rowNumber) {
        if (StringUtils.isEmpty(cellValue)) {
            if (required) {
                return new ResultMessage(MESSAGE_MISSING_REQUIRED_FIELD, ResultMessageStatus.ERROR,
                        "Cell value not given but required for column '"
                                + columnDef.getColumnHeadname() + "' in row no " + rowNumber + ".", rowNumber, columnDef);
            }
            return null;
        }
        if (patternRegExp != null) {
            try {
                if (!cellValue.matches(patternRegExp)) {
                    return new ResultMessage(MESSAGE_PATTERN_MISMATCH, ResultMessageStatus.ERROR,
                            "Cell value '" + cellValue + "' doesn't match required pattern '" + patternRegExp + " for column '"
                                    + columnDef.getColumnHeadname() + "' in row no " + rowNumber + ".", rowNumber, columnDef, cellValue);
                }
            } catch (PatternSyntaxException ex) {
                log.error("Pattern syntax error for regex for column '" + columnDef.getColumnHeadname() + "': '" + patternRegExp
                        + "': " + ex.getMessage(), ex);
                return null;
            }
        }
        Integer firstOccurrenceRowNumber = isUnique(cellValue, rowNumber);
        if (firstOccurrenceRowNumber != null) {
            return new ResultMessage(MESSAGE_VALUE_NOT_UNIQUE, ResultMessageStatus.ERROR,
                    "Cell value '" + cellValue + "' isn't unique for column '"
                            + columnDef.getColumnHeadname() + "' in row no " + rowNumber + ". It's already used in row number "
                            + firstOccurrenceRowNumber + ".", rowNumber, firstOccurrenceRowNumber, columnDef, cellValue);
        }
        return null;
    }

    @Override
    public void readCell(Cell cell, int rowNumber) {
        String cellValue = PoiHelper.getValueAsString(cell);
        ResultMessage resultMessage = isValid(cellValue, rowNumber);
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

    public List<ResultMessage> getValidationErrors() {
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
}
