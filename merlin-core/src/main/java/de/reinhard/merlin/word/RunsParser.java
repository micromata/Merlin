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
            // loop until no further replacements are found.
            buildText(); // Rebuild text after every variable substitution.
            //logDebugRuns("Runs at step " + paranoiaCounter + ": ");
            if (paranoiaCounter++ > 1000) {
                throw new IllegalStateException("End-less loop protection!");
            }
        } while (replaceVariables());
        //ogDebugRuns("Runs after step " + paranoiaCounter + ": ");
    }

    private boolean replaceVariables() {
        Matcher matcher = variablePattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group(1);
            String value = variables.get(group);
            if (value == null) {
                continue; // Variable not found. Ignore this finding.
            }
            if (variablePattern.matcher(value).matches()) {
                // Avoids endless-loop if variable expression is replaced by variable expression.
                value = value.replace("${", "_{") ;
            }
            int start = matcher.start();
            int end = matcher.end();
            Position startPos = getRunIdxAndPosition(start);
            Position endPos = getRunIdxAndPosition(end - 1);
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
                return true;
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
            /*
            if (log.isDebugEnabled()) {
                log.debug("Run[" + i + "]: " + text + ", length=" + runSizes[i]);
            }*/
            sb.append(text);
        }
        text = sb.toString();
        /*if (log.isDebugEnabled()) {
            log.debug(text);
        }*/
    }

    class Position {
        Position(int runIndex, int runPos) {
            this.runIndex = runIndex;
            this.runCharAt = runPos;
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
}
