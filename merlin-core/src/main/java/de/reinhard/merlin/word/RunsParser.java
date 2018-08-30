package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word devides text unpredicable in different runs. So the replacement of variables can't be easily done by processing
 * single runs.
 */
public class RunsParser {
    private Logger log = LoggerFactory.getLogger(RunsParser.class);
    private static Pattern variablePattern = Pattern.compile("\\$\\{(\\w*)\\}");
    private int currentRunIdx;
    private int currentCharIdx;
    private int[] runSizes;
    private String text;
    private Map<String, String> variables;

    private List<XWPFRun> runs;

    public RunsParser(List<XWPFRun> runs, Map<String, String> variables) {
        this.runs = runs;
        this.variables = variables;
    }

    public void run() {
        if (runs == null || runs.size() == 0) {
            return;
        }
        int paranoiaCounter = 0;
        do {
            buildText(); // Rebuild text after every variable substitution.
            replaceVariables();
            if (paranoiaCounter++ > 1000) {
                throw new IllegalStateException("End-less loop protection!");
            }
        } while (replaceVariables());
    }

    private boolean replaceVariables() {
        Matcher matcher = variablePattern.matcher(text);
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
            XWPFRun run = runs.get(startPos.runIndex);
            String text;
            if (startPos.runPos == 0) {
                text = value;
            } else {
                try {
                    text = run.getText(0).substring(0, startPos.runPos - 1) + value;
                } catch (Exception ex) {
                    log.info("");
                    text = "";
                }
            }
            run.setText(text, 0);
            for (int i = startPos.runIndex + 1; i < endPos.runIndex - 1; i++) {
                runs.get(i).setText("", 0);
            }
            run = runs.get(endPos.runIndex);
            log.debug("Run: " + run.getText(0) + ", runPos=" + endPos.runPos);
            text = run.getText(0);
            if (text == null || text.length() >= endPos.runPos) {
                run.setText("", 0);
            } else {
                log.debug("" + endPos.runPos);
                run.setText(text.substring(endPos.runPos), 0);
            }
            return true;
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

    void buildText() {
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
            if (log.isDebugEnabled()) {
                log.debug("Run[" + i + "]: " + text + ", length=" + runSizes[i]);
            }
            sb.append(text);
        }
        text = sb.toString();
        if (log.isDebugEnabled()) {
            log.debug(text);
        }
    }

    class Position {
        Position(int runIndex, int runPos) {
            this.runIndex = runIndex;
            this.runPos = runPos;
        }

        int runIndex;
        int runPos;
    }
}
