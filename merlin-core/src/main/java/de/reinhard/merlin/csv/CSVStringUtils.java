package de.reinhard.merlin.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CSVStringUtils {
    private static Logger log = LoggerFactory.getLogger(CSVStringUtils.class);

    private enum QuotationStyle {SINGLE, DOUBLE, NONE}

    ;

    private static final String VALUE_SINGLE_START_QUOTS = "‚'’`´";
    private static final String VALUE_SINGLE_END_QUOTS = "‘'’`´";
    private static final String VALUE_DOUBLE_START_QUOTS = "„\"“”";
    private static final String VALUE_DOUBLE_END_QUOTS = "“”\"";
    private static final String VALUE_SEPARATOR_CHARS = ",;,:";

    public static String[] parseStringList(String str) {
        return parseStringList(str, true);
    }

    public static String[] parseStringList(String str, boolean trim) {
        if (str == null || str.length() == 0) {
            return new String[0];
        }
        List<String> params = new ArrayList<>();
        StringBuilder value = null;
        QuotationStyle quotation = null;
        boolean separatorCharExpected = false;
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            if (value == null) {
                // Parsing for next value.
                if (Character.isWhitespace(ch)) {
                    continue;
                }
                if (VALUE_SEPARATOR_CHARS.indexOf(ch) >= 0) {
                    if (separatorCharExpected == true) {
                        // Separator char read.
                        separatorCharExpected = false;
                        continue;
                    }
                    log.warn("Separator char received unexpected. Continuing anyhow: " + str);
                    continue;
                }
                if (separatorCharExpected == true) {
                    log.debug("Separator char expected, but received: '" + ch + "'. Continuing anyhow: " + str);
                }
                // Not a separator char and not a white space. Beginning of value expected.
                if (VALUE_SINGLE_START_QUOTS.indexOf(ch) >= 0) {
                    // Value is single qoted:
                    quotation = QuotationStyle.SINGLE;
                    value = new StringBuilder();
                    continue;
                } else if (VALUE_DOUBLE_START_QUOTS.indexOf(ch) >= 0) {
                    // Value is double quoted:
                    quotation = QuotationStyle.DOUBLE;
                    value = new StringBuilder();
                    continue;
                } else {
                    quotation = QuotationStyle.NONE;
                }
                // Assuming an unquoted value.
                value = new StringBuilder();
                value.append(ch);
                continue;
            }
            // Reading value or end of value:
            if (quotation == QuotationStyle.SINGLE && VALUE_SINGLE_END_QUOTS.indexOf(ch) >= 0 ||
                    quotation == QuotationStyle.DOUBLE && VALUE_DOUBLE_END_QUOTS.indexOf(ch) >= 0) {
                // End of value reached.
                add(params, value.toString(), trim);
                value = null;
                quotation = null;
                separatorCharExpected = true;
                continue;
            }
            if (i == str.length() - 1) {
                // End reached. Add current character:
                if (quotation != QuotationStyle.NONE) {
                    log.warn("Terminating quotation mark is missed (continuing anyhow): " + str);
                }
                // Store last character.
                value.append(ch);
                break;
            }
            if (quotation == QuotationStyle.NONE && VALUE_SEPARATOR_CHARS.indexOf(ch) >= 0) {
                add(params, value.toString(), trim);
                value = null;
                quotation = null;
                separatorCharExpected = false;
                continue;
            }
            value.append(ch);
        }
        if (value != null) {
            add(params, value.toString(), trim);
        }
        String[] result = new String[params.size()];
        params.toArray(result);
        return result;
    }

    private static void add(List<String> params, String value, boolean trim) {
        if (trim) {
            params.add(value.trim());
        } else {
            params.add(value);
        }
    }
}
