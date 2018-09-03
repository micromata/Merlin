package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word devides text unpredicable in different runs. So the replacement of variables can't be easily done by processing
 * single runs.
 */
public class RunsProcessor {
    private static Logger log = LoggerFactory.getLogger(RunsProcessor.class);
    static final String IDENTIFIER_REGEXP = "[a-zA-Z_][a-zA-Z\\d_]*";
    static final Pattern defaultVariablePattern = Pattern.compile("\\$\\{\\s*(" + IDENTIFIER_REGEXP + ")\\s*\\}");
    private int[] runSizes;
    private Pattern variablePattern;
    private String runsText;
    private TreeSet<ReplaceEntry> replaceEntries;

    private List<XWPFRun> runs;

    public RunsProcessor(List<XWPFRun> runs) {
        this.runs = runs;
        this.variablePattern = defaultVariablePattern;
    }

    public void replace(Map<String, ?> variables) {
        if (runs == null || runs.size() == 0) {
            return;
        }
        replaceEntries = new TreeSet<>();
        String runsText = getText();
        Matcher matcher = variablePattern.matcher(runsText);
        //log.debug("Start pos: " + lastPos);
        while (matcher.find()) {
            String group = matcher.group(1);
            Object objectValue = variables.get(group);
            if (objectValue == null) {
                continue; // Variable not found. Ignore this finding.
            }
            String value = objectValue.toString();
            int start = matcher.start();
            int end = matcher.end();
            replaceEntries.add(new ReplaceEntry(start, end, value));
        }
        Iterator<ReplaceEntry> it = replaceEntries.descendingIterator();
        while (it.hasNext()) {
            ReplaceEntry entry = it.next();
            DocumentPosition startPos = getRunIdxAndPosition(-1, entry.start);
            replaceText(startPos, getRunIdxAndPosition(-1, entry.end - 1), entry.newText);
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
        XWPFRun run = runs.get(startPos.getRunIndex());
        String text;
        text = run.getText(0);
        StringBuilder sb = new StringBuilder();
        sb.append(text.substring(0, startPos.getRunCharAt()))
                .append(newValue);
        if (startPos.getRunIndex() == endPos.getRunIndex()) { // Variable substitution in one signle run.
            if (endPos.getRunCharAt() < text.length()) {
                // Append the tail after ${var}:
                sb.append(text.substring(endPos.getRunCharAt() + 1));
            }
            setText(run, sb.toString());
            // Continue with index after actual:
            return;
        }
        setText(run, sb.toString());
        for (int idx = startPos.getRunIndex() + 1; idx < endPos.getRunIndex(); idx++) {
            // Processing runs in between.
            setText(runs.get(idx), "");

        }
        // Processing last affected run:
        run = runs.get(endPos.getRunIndex());
        text = run.getText(0);
        setText(run, text.substring(endPos.getRunCharAt() + 1));
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
