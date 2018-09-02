package de.reinhard.merlin.word;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word devides text unpredicable in different runs. So the replacement of variables can't be easily done by processing
 * single runs.
 */
public class RunsProcessor {
    private static Logger log = LoggerFactory.getLogger(RunsProcessor.class);
    private static final String IDENTIFIER_REGEXP = "[a-zA-Z_][a-zA-Z\\d_]*";
    //private static final String QUOTED_VALUE = "([\"„“][^\"“]*[\"“]|['‚‘][^'‘]*['‘])";
    private static final String VALUE_SINGLE_START_QUOTS = "‚'";
    private static final String VALUE_SINGLE_END_QUOTS = "‘'";
    private static final String VALUE_DOUBLE_START_QUOTS = "„\"";
    private static final String VALUE_DOUBLE_END_QUOTS = "“\"";
    private static final String VALUE_SEPARATOR_CHARS = ",;,:";

    private enum QuotationStyle {SINGLE, DOUBLE, NONE}

    ;

    // ${identifier}
    static Pattern variablePattern = Pattern.compile("\\$\\{\\s*(" + IDENTIFIER_REGEXP + ")\\s*\\}");
    // ${if identifier='value'}
    static Pattern beginIfPattern = Pattern.compile("\\{if\\s+(" + IDENTIFIER_REGEXP + ")\\s*(!?=|!?in)\\s*(.*)\\s*\\}");
    static Pattern endIfPattern = Pattern.compile("\\{endif\\}");
    private int currentRunIdx;
    private int currentCharIdx;
    private int[] runSizes;
    private Map<String, String> variables;

    private List<XWPFRun> runs;

    public RunsProcessor(List<XWPFRun> runs, Map<String, String> variables) {
        this.runs = runs;
        this.variables = variables;
    }

    public void run() {
        if (runs == null || runs.size() == 0) {
            return;
        }
        int paranoiaCounter = 0;
        String text;
        Position startPos = null;
        do {
            // loop until no further replacements are found.
            text = buildText(); // Rebuild text after every variable substitution.
            //logDebugRuns("Runs at step " + paranoiaCounter + ": ");
            if (paranoiaCounter++ > 1000) {
                throw new IllegalStateException("End-less loop protection! " + "text = " + text);
            }
            startPos = replaceVariables(text, startPos);
        } while (startPos != null);
        //logDebugRuns("Runs after step " + paranoiaCounter + ": ");
    }

    /**
     * @param runsText Whole text concatenated from all runs.
     * @param lastPos  Position of last replaced region or null for starting.
     * @return
     */
    private Position replaceVariables(String runsText, Position lastPos) {
        Matcher matcher = variablePattern.matcher(runsText);
        //log.debug("Start pos: " + lastPos);
        while (matcher.find()) {
            String group = matcher.group(1);
            String value = variables.get(group);
            if (value == null) {
                continue; // Variable not found. Ignore this finding.
            }
            int start = matcher.start();
            int end = matcher.end();
            Position startPos = getRunIdxAndPosition(start);
            Position endPos = getRunIdxAndPosition(end - 1);
            if (startPos.compareTo(lastPos) <= 0) {
                // startPos is not after last pos.
                continue;
            }
            XWPFRun run = runs.get(startPos.runIndex);
            String text;
            text = run.getText(0);
            StringBuilder sb = new StringBuilder();
            sb.append(text.substring(0, startPos.runCharAt))
                    .append(value);
            if (startPos.runIndex == endPos.runIndex) { // Variable substitution in one signle run.
                if (endPos.runCharAt < text.length()) {
                    // Append the tail after ${var}:
                    sb.append(text.substring(endPos.runCharAt + 1));
                }
                run.setText(sb.toString(), 0);
                // Continue with index after actual:
                return new Position(endPos.runIndex, Integer.max(endPos.runCharAt, startPos.runCharAt + value.length()));
            }
            run.setText(sb.toString(), 0);
            for (int idx = startPos.runIndex + 1; idx < endPos.runIndex; idx++) {
                // Processing runs in between.
                runs.get(idx).setText("", 0);

            }
            // Processing last affected run:
            run = runs.get(endPos.runIndex);
            text = run.getText(0);
            run.setText(text.substring(endPos.runCharAt + 1), 0);
            return endPos;
        }
        return null;
    }

    public boolean processConditionals(boolean hidden) {
        if (hidden) {
            //      buildText();
            //    Matcher beginIfMatcher = beginIfPattern.matcher(text);

        } else {
        }
        return false;
    }

    Position getRunIdxAndPosition(int pos) {
        int length = 0;
        int preLength = 0;
        for (int i = 0; i < runSizes.length; i++) {
            length += runSizes[i];
            if (pos < length) {
                return new Position(i, pos - preLength);
            }
            preLength = length;
        }
        return new Position(-1, -1);
    }

    /**
     * @return Whole text concatenated from all runs.
     */
    String buildText() {
        StringBuilder sb = new StringBuilder();
        runSizes = new int[runs.size()];
        int i = -1;
        for (XWPFRun run : runs) {
            ++i;
            String text = run != null ? run.getText(0) : null;
            if (text == null) {
                runSizes[i] = 0;
                continue;
            }
            runSizes[i] = text.length();
            /*
            if (log.isDebugEnabled()) {
                log.debug("Run[" + i + "]: " + text + ", length=" + runSizes[i]);
            }*/
            sb.append(text);
        }
        return sb.toString();
        /*if (log.isDebugEnabled()) {
            log.debug(text);
        }*/
    }

    class Position implements Comparable<Position> {
        Position(int runIndex, int runPos) {
            this.runIndex = runIndex;
            this.runCharAt = runPos;
        }

        @Override
        public int compareTo(Position o) {
            if (o == null) {
                // this is greater than other (null).
                return 1;
            }
            return new CompareToBuilder()
                    .append(this.runIndex, o.runIndex)
                    .append(this.runCharAt, o.runCharAt)
                    .toComparison();
        }

        @Override
        public String toString() {
            return "idx=" + runIndex + ", charAt=" + runIndex;
        }

        int runIndex;
        int runCharAt;
    }

    private void logDebugRuns(String prefix) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (XWPFRun run : runs) {
            sb.append(i++).append("=[").append(run.getText(0)).append("]");
        }
        log.debug(prefix + sb.toString());
    }

    static String[] parseStringList(String str) {
        if (str == null || str.length() == 0) {
            return new String[0];
        }
        List<String> params = new LinkedList<>();
        StringBuilder value = null;
        QuotationStyle quotation = null;
        boolean separatorCharExpected = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
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
                params.add(value.toString());
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
                params.add(value.toString());
                value = null;
                quotation = null;
                separatorCharExpected = false;
                continue;
            }
            value.append(ch);
        }
        if (value != null) {
            params.add(value.toString());
        }
        String[] result = new String[params.size()];
        params.toArray(result);
        return result;
    }
}
