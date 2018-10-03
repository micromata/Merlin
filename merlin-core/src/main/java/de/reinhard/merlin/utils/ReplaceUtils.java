package de.reinhard.merlin.utils;

import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceUtils {
    public static final String IDENTIFIER_REGEXP = "[a-zA-Z_][a-zA-Z\\d_]*";
    public static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{\\s*(" + IDENTIFIER_REGEXP + ")\\s*\\}");
    public static final Pattern COMMENT_PATTERN = Pattern.compile("\\{\\*[^\\}]*\\}");
    public static final String ALLOWED_FILENAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-";
    public static final String PRESERVED_FILENAME_CHARS = "\"*/:<>?\\|";
    public static final char FILENAME_REPLACE_CHAR = '_';

    private static Map<Character, String> umlautReplacementMap;

    static {
        umlautReplacementMap = new HashMap<>();
        umlautReplacementMap.put('Ä', "Ae");
        umlautReplacementMap.put('Ö', "Oe");
        umlautReplacementMap.put('Ü', "Ue");
        umlautReplacementMap.put('ä', "ae");
        umlautReplacementMap.put('ö', "oe");
        umlautReplacementMap.put('ü', "ue");
        umlautReplacementMap.put('ß', "ss");
    }

    public static List<ReplaceEntry> createReplaceEntries(String text, Map<String, ?> variables) {
        List<ReplaceEntry> replaceEntries = new ArrayList();
        createReplaceEntries(text, replaceEntries, variables);
        return replaceEntries;
    }

    /**
     * Creates replace entries for variables and comments.
     *
     * @param text
     * @param replaceEntries
     * @param variables
     */
    public static void createReplaceEntries(String text, List<ReplaceEntry> replaceEntries, Map<String, ?> variables) {
        Matcher matcher = VARIABLE_PATTERN.matcher(text);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object objectValue = variables.get(variableName);
            if (objectValue == null) {
                continue; // Variable not found. Ignore this finding.
            }
            String value = objectValue.toString();
            int start = matcher.start();
            int end = matcher.end();
            replaceEntries.add(new ReplaceEntry(start, end, value));
        }
        matcher = COMMENT_PATTERN.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            replaceEntries.add(new ReplaceEntry(start, end, ""));
        }
        Collections.sort(replaceEntries, Collections.reverseOrder());
    }

    /**
     * Replaces all occurrences.
     *
     * @param text
     * @param replaceEntries
     * @return
     */
    public static String replace(String text, List<ReplaceEntry> replaceEntries) {
        for (ReplaceEntry entry : replaceEntries) {
            StringBuilder sb = new StringBuilder();
            sb.append(text.substring(0, entry.start)).append(entry.newText);
            if (entry.end < text.length()) {
                // Append the tail after ${var}:
                sb.append(text.substring(entry.end));
            }
            text = sb.toString();
        }
        return text;
    }

    public static String replace(String text, Map<String, ?> variables) {
        List<ReplaceEntry> replaceEntries = createReplaceEntries(text, variables);
        return replace(text, replaceEntries);
    }

    /**
     * Preserved characters (Windows): 0x00-0x1F 0x7F " * / : < > ? \ |
     * Preserved characters (Mac OS): ':'
     * Preserved characters (Unix): '/'
     * Max length: 255
     *
     * @param filename
     * @param reducedCharsOnly if true, only {@link #ALLOWED_FILENAME_CHARS} are allowed and German Umlaute are replaced
     *                         'Ä'->'Ae' etc. If not, all characters excluding {@link #PRESERVED_FILENAME_CHARS} are allowed and
     *                         all white spaces will be replaced by ' ' char.
     * @return
     */

    public static String encodeFilename(String filename, boolean reducedCharsOnly) {
        if (StringUtils.isEmpty(filename)) {
            return "file";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filename.length(); i++) {
            char ch = filename.charAt(i);
            if (reducedCharsOnly) {
                if (umlautReplacementMap.containsKey(ch)) {
                    sb.append(umlautReplacementMap.get(ch));
                } else if (ALLOWED_FILENAME_CHARS.indexOf(ch) >= 0) {
                    sb.append(ch);
                } else {
                    sb.append(FILENAME_REPLACE_CHAR);
                }
            } else {
                if (ch <= 31 || ch == 127) { // Not 0x00-0x1F and not 0x7F
                    sb.append(FILENAME_REPLACE_CHAR);
                } else if (PRESERVED_FILENAME_CHARS.indexOf(ch) >= 0) {
                    sb.append(FILENAME_REPLACE_CHAR);
                } else if (Character.isWhitespace(ch)) {
                    sb.append(' ');
                } else {
                    sb.append(ch);
                }
            }
        }
        String result = sb.toString();
        if (result.length() > 255) {
            return result.substring(0, 255);
        }
        return result;
    }
}
