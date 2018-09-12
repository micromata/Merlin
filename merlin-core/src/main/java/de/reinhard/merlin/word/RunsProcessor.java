package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word devides text unpredicable in different runs. So the replacement of variables can't be easily done by processing
 * single runs.
 */
public class RunsProcessor {
    private static Logger log = LoggerFactory.getLogger(RunsProcessor.class);
    public static final String IDENTIFIER_REGEXP = "[a-zA-Z_][a-zA-Z\\d_]*";
    static final Pattern defaultVariablePattern = Pattern.compile("\\$\\{\\s*(" + IDENTIFIER_REGEXP + ")\\s*\\}");
    private int[] runSizes;
    private Pattern variablePattern;
    private XWPFParagraph paragraph;
    private String runsText;
    private List<ReplaceEntry> replaceEntries;

    private List<XWPFRun> runs;

    public RunsProcessor(XWPFParagraph paragraph) {
        this(paragraph.getRuns());
        this.paragraph = paragraph;
    }

    /**
     * Only for test class.
     *
     * @param runs
     */
    RunsProcessor(List<XWPFRun> runs) {
        this.runs = runs;
        this.variablePattern = defaultVariablePattern;
    }

    public void replace(Map<String, ?> variables) {
        if (runs == null || runs.size() == 0) {
            return;
        }
        replaceEntries = new ArrayList<>();
        String runsText = getText();
        Matcher matcher = variablePattern.matcher(runsText);
        //log.debug("Start pos: " + lastPos);
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
        Collections.sort(replaceEntries, Collections.reverseOrder());
        for (ReplaceEntry entry : replaceEntries) {
            DocumentPosition startPos = getRunIdxAndPosition(-1, entry.start);
            replaceText(startPos, getRunIdxAndPosition(-1, entry.end - 1), entry.newText);
        }
    }

    void scanVariables(Set<String> variables) {
        if (runs == null || runs.size() == 0) {
            return;
        }
        String runsText = getText();
        Matcher matcher = variablePattern.matcher(runsText);
        while (matcher.find()) {
            String variableName = matcher.group(1);
            variables.add(variableName);
        }
    }

    DocumentPosition getEnd(int bodyElementNo) {
        int lastRun = runs.size() - 1;
        return new DocumentPosition(bodyElementNo, lastRun, runs.get(lastRun).getText(0).length() - 1);
    }

    /**
     * @param startPos
     * @param endPos
     * @param newValue
     * @return
     */
    void replaceText(DocumentPosition startPos, DocumentPosition endPos, String newValue) {
        XWPFRun firstRun = runs.get(startPos.getRunIndex());
        String text;
        text = firstRun.getText(0);
        StringBuilder sb = new StringBuilder();
        sb.append(text.substring(0, startPos.getRunCharAt())).append(newValue);
        if (startPos.getRunIndex() == endPos.getRunIndex()) { // Variable substitution in one signle run.
            if (endPos.getRunCharAt() + 1 < text.length()) {
                // Append the tail after ${var}:
                sb.append(text.substring(endPos.getRunCharAt() + 1));
            }
            setText(firstRun, sb.toString());
            return;
        }
        // Setting text of first run:
        setText(firstRun, sb.toString());

        // Processing last affected run:
        XWPFRun lastRun = runs.get(endPos.getRunIndex());
        text = lastRun.getText(0);
        if (endPos.getRunCharAt() + 1 < text.length()) {
            setText(lastRun, text.substring(endPos.getRunCharAt() + 1));
        } else {
            if (paragraph != null) {
                paragraph.removeRun(endPos.getRunIndex());
            } else {
                setText(lastRun, "");
            }
        }

        // Processing all runs between 1st and last run:
        for (int idx = endPos.getRunIndex() - 1; idx > startPos.getRunIndex(); idx--) {
            // Processing runs in between.
            if (paragraph != null) {
                paragraph.removeRun(idx);
            } else {
                setText(runs.get(idx), "");
            }
        }
    }

    private void setText(XWPFRun run, String text) {
        run.setText(text, 0);
        runsText = null; // Force reconstructing of text.
    }

    DocumentPosition getRunIdxAndPosition(int bodyElementNumber, int pos) {
        int length = 0;
        int preLength = 0;
        for (int i = 0; i < runSizes.length; i++) {
            length += runSizes[i];
            if (pos < length) {
                return new DocumentPosition(bodyElementNumber, i, pos - preLength);
            }
            preLength = length;
        }
        return null;
    }

    /**
     * @return Whole text concatenated from all runs.
     */
    public String getText() {
        if (runsText != null) {
            return runsText;
        }
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
            // if (log.isDebugEnabled()) { log.debug("Run[" + i + "]: " + text + ", length=" + runSizes[i]); }
            sb.append(text);
        }
        runsText = sb.toString();
        return runsText;
        // if (log.isDebugEnabled()) { log.debug(text); }
    }

    public Pattern getVariablePattern() {
        return variablePattern;
    }

    public void setVariablePattern(Pattern variablePattern) {
        this.variablePattern = variablePattern;
    }

    private void logDebugRuns(String prefix) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (XWPFRun run : runs) {
            sb.append(i++).append("=[").append(run.getText(0)).append("]");
        }
        log.debug(prefix + sb.toString());
    }

    private class ReplaceEntry implements Comparable<ReplaceEntry> {
        int start;
        int end;
        String newText;

        ReplaceEntry(int start, int end, String newText) {
            this.start = start;
            this.end = end;
            this.newText = newText;
        }

        @Override
        public int compareTo(ReplaceEntry o) {
            if (start == o.start) {
                return 0;
            } else if (start < o.start) {
                return -1;
            }
            return 1;
        }
    }
}
