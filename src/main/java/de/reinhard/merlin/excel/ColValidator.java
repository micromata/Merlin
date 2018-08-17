package de.reinhard.merlin.excel;

import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.ResultMessageStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.PatternSyntaxException;

public class ColValidator {
    /**
     * Parameter: rowNumber, colNumber, colName
     */
    public static final String MESSAGE_MISSING_REQUIRED_FIELD = ColValidator.class.getName() + ":MESSAGE_MISSING_REQUIRED_FIELD";
    /**
     * Parameter: rowNumber, colNumber, colName, cellValue
     */
    public static final String MESSAGE_PATTERN_MISMATCH = ColValidator.class.getName() + ":MESSAGE_PATTERN_MISMATCH";
    private static final Logger log = Logger.getLogger(ColValidator.class);
    private String colName;
    private int colNumber;
    private boolean required;
    private boolean unique;
    private String patternRegExp;
    private boolean colHeadFound;
    // Used for unique constraint.
    private Set<String> entries = new TreeSet<>();

    /**
     * @param colNumber Number of col: 1 (A), 2 (B), ...
     */
    public ColValidator(int colNumber) {
        this.colNumber = colNumber;
    }

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
                new ResultMessage(MESSAGE_MISSING_REQUIRED_FIELD, ResultMessageStatus.ERROR,
                        "Cell value not given but required for column '"
                                + colName + "' in row no " + rowNumber + ".", rowNumber, colNumber, colName);
            }
            return null;
        }
        if (patternRegExp != null) {
            try {
                if (!cellValue.matches(patternRegExp)) {
                    new ResultMessage(MESSAGE_PATTERN_MISMATCH, ResultMessageStatus.ERROR,
                            "Cell value '" + cellValue + "' doesn't match required pattern '" + patternRegExp + " for column '"
                                    + colName + "' in row no " + rowNumber + ".", rowNumber, colNumber, colName, cellValue);
                }
            } catch (PatternSyntaxException ex) {
                log.error("Pattern syntax error for regex for column '" + colName + "': '" + patternRegExp
                        + "': " + ex.getMessage(), ex);
                return null;
            }
        }
        return null;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public int getColNumber() {
        return colNumber;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isUnique() {
        return unique;
    }

    /**
     * @return true, if sheet parser found col with specified column header.
     */
    public boolean isColHeadFound() {
        return colHeadFound;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
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
