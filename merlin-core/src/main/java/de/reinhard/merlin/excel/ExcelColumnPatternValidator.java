package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ExcelColumnPatternValidator extends ExcelColumnValidator {
    /**
     * Parameter: Sheet name, Column in letter format: (A, B, ..., AA, AB, ...), Column head name, Row number,
     * Cell value, pattern
     */
    public static final String MESSAGE_PATTERN_MISMATCH = "merlin.excel.validation_error.pattern_mismatch";
    public static final String MESSAGE_NOT_EMAIL = "merlin.excel.validation_error.email_expected";

    private Logger log = LoggerFactory.getLogger(ExcelColumnPatternValidator.class);
    private String patternRegExp;
    private Pattern pattern;
    private String validationErrorMessageId;
    private int flags;

    public ExcelColumnPatternValidator() {
    }

    /**
     * @param patternRegExp
     */
    public ExcelColumnPatternValidator(String patternRegExp) {
        this.flags = flags;
        setPatternRegExp(patternRegExp);
        //Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    }

    /**
     * @param patternRegExp
     * @param flags         @see {@link Pattern#compile(String, int)}
     */
    public ExcelColumnPatternValidator(String patternRegExp, int flags) {
        this.flags = flags;
        setPatternRegExp(patternRegExp, flags);
        //Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    }

    /**
     * Checks pattern match if {@link #patternRegExp} is given, otherwise returns null.
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
        if (pattern != null) {
            Matcher m = pattern.matcher(cellValue);
            if (!m.find()) {
                if (validationErrorMessageId != null) {
                    return createValidationError(validationErrorMessageId, rowNumber, cellValue, patternRegExp);
                } else {
                    return createValidationErrorPatternMismatch(rowNumber, cellValue, patternRegExp);
                }
            }
        }
        return null;
    }

    public String getPatternRegExp() {
        return patternRegExp;
    }

    /**
     * @param patternRegExp String patternRegExp to validate. If null, no patternRegExp match will be validated.
     */
    public void setPatternRegExp(String patternRegExp) {
        this.patternRegExp = patternRegExp;
        this.pattern = Pattern.compile(patternRegExp);
    }

    /**
     * @param patternRegExp String patternRegExp to validate. If null, no patternRegExp match will be validated.
     * @param flags         @see {@link Pattern#compile(String, int)}
     * @see String#matches(String)
     */
    public void setPatternRegExp(String patternRegExp, int flags) {
        this.patternRegExp = patternRegExp;
        this.pattern = Pattern.compile(patternRegExp, flags);
    }

    /**
     * Set the pattern for checking e-mail addresses: ^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,6}$ and flag {@link Pattern#CASE_INSENSITIVE}.
     *
     * @return this for chaining.
     */
    public ExcelColumnPatternValidator setEMailPattern() {
        setPatternRegExp("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        this.validationErrorMessageId = MESSAGE_NOT_EMAIL;
        return this;
    }

    /**
     * For displaying the expected pattern more user-friendly. This message is only used for display purposes. If not
     * given, the patternRegExp String will be used for user messages.
     *
     * @param validationErrorMessageId id of the message of resource bundle.
     * @return this for chaining.
     */
    public ExcelColumnPatternValidator setValidationErrorMessageId(String validationErrorMessageId) {
        this.validationErrorMessageId = validationErrorMessageId;
        return this;
    }

    ExcelValidationErrorMessage createValidationErrorPatternMismatch(int rowNumber, Object cellValue, String
            patternRegExp) {
        return createValidationError(MESSAGE_PATTERN_MISMATCH, rowNumber, cellValue, patternRegExp);
    }
}

