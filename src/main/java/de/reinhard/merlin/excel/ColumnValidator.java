package de.reinhard.merlin.excel;

import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.ResultMessageStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellReference;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.PatternSyntaxException;

public class ColumnValidator {
    /**
     * Parameter: rowNumber, columnNumber, columnHeadname
     */
    public static final String MESSAGE_MISSING_REQUIRED_FIELD = ColumnValidator.class.getName() + ":MESSAGE_MISSING_REQUIRED_FIELD";
    /**
     * Parameter: rowNumber, columnNumber, columnHeadname, cellValue
     */
    public static final String MESSAGE_PATTERN_MISMATCH = ColumnValidator.class.getName() + ":MESSAGE_PATTERN_MISMATCH";
    private static final Logger log = Logger.getLogger(ColumnValidator.class);
    private String columnHeadname;
    private int columnNumber;
    private boolean required;
    private boolean unique;
    private String patternRegExp;
    private boolean columnHeadnameFound;
    // Used for unique constraint.
    private Set<String> entries = new TreeSet<>();

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
                                + columnHeadname + "' in row no " + rowNumber + ".", rowNumber, columnNumber, columnHeadname);
            }
            return null;
        }
        if (patternRegExp != null) {
            try {
                if (!cellValue.matches(patternRegExp)) {
                    new ResultMessage(MESSAGE_PATTERN_MISMATCH, ResultMessageStatus.ERROR,
                            "Cell value '" + cellValue + "' doesn't match required pattern '" + patternRegExp + " for column '"
                                    + columnHeadname + "' in row no " + rowNumber + ".", rowNumber, columnNumber, columnHeadname, cellValue);
                }
            } catch (PatternSyntaxException ex) {
                log.error("Pattern syntax error for regex for column '" + columnHeadname + "': '" + patternRegExp
                        + "': " + ex.getMessage(), ex);
                return null;
            }
        }
        return null;
    }

    public String getColumnHeadname() {
        return columnHeadname;
    }

    public void setColumnHeadname(String columnHeadname) {
        this.columnHeadname = columnHeadname;
    }

    /**
     * @return Number of col: 1 (A), 2 (B), ...
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * @return Column number as Excel letter: A, B, ..., AA, AB, ...
     */
    public String getColumnNumberString() {
        return CellReference.convertNumToColString(columnNumber);
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
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
    public boolean isColumnHeadnameFound() {
        return columnHeadnameFound;
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
