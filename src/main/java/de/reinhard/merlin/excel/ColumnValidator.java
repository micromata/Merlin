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
     * Parameter: rowNumber, ExcelColumnDef
     */
    public static final String MESSAGE_MISSING_REQUIRED_FIELD = ColumnValidator.class.getName() + ":MESSAGE_MISSING_REQUIRED_FIELD";
    /**
     * Parameter: rowNumber, ExcelColumnDef, cellValue
     */
    public static final String MESSAGE_PATTERN_MISMATCH = ColumnValidator.class.getName() + ":MESSAGE_PATTERN_MISMATCH";
    private static final Logger log = Logger.getLogger(ColumnValidator.class);
    private ExcelColumnDef columnDef;
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
                                + columnDef.getColumnHeadname() + "' in row no " + rowNumber + ".", rowNumber, columnDef);
            }
            return null;
        }
        if (patternRegExp != null) {
            try {
                if (!cellValue.matches(patternRegExp)) {
                    new ResultMessage(MESSAGE_PATTERN_MISMATCH, ResultMessageStatus.ERROR,
                            "Cell value '" + cellValue + "' doesn't match required pattern '" + patternRegExp + " for column '"
                                    + columnDef.getColumnHeadname() + "' in row no " + rowNumber + ".", rowNumber, columnDef, cellValue);
                }
            } catch (PatternSyntaxException ex) {
                log.error("Pattern syntax error for regex for column '" + columnDef.getColumnHeadname() + "': '" + patternRegExp
                        + "': " + ex.getMessage(), ex);
                return null;
            }
        }
        return null;
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

    void setColumnDef(ExcelColumnDef columnDef) {
        this.columnDef = columnDef;
    }
}
