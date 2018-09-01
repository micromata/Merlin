package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RunsParserTest {
    private Logger log = LoggerFactory.getLogger(RunsParserTest.class);

    @Test
    public void readWordTest() throws Exception {
        Map<String, String> variables = new HashMap<>();
        assertRunIdx(1, 0, 5, "12345", "${Hallo}");
        assertRunIdx(0, 2, 2, "12345", "${Hallo}");
        assertRunIdx(0, 0, 0, "$");
        assertRunIdx(-1, -1, 0, "");
        assertRunIdx(0, 0, 0, "$", "{Mitarbeiter}");
        assertRunIdx(1, 12, 13, "$", "{Mitarbeiter}");
    }

    private XWPFRun createRun(String text) {
        XWPFRun run = mock(XWPFRun.class);
        when(run.getText(0)).thenReturn(text);
        return run;
    }

    private void assertRunIdx(int runIdx, int runIdxPos, int pos, String... runStrings) {
        List<XWPFRun> runs = new LinkedList<>();
        for (String str : runStrings) {
            runs.add(createRun(str));
        }
        RunsProcessor runsParser = new RunsProcessor(runs, null);
        runsParser.buildText();
        RunsProcessor.Position position = runsParser.getRunIdxAndPosition(pos);
        assertEquals(runIdx, position.runIndex, "Run index.");
        assertEquals(runIdxPos, position.runCharAt, "Run index position.");
    }

    @Test
    public void parseStringList() {
        assertArrayEquals(new String[0], RunsProcessor.parseStringList(null));
        assertArrayEquals(new String[0], RunsProcessor.parseStringList(""));
        assertArrayEquals(new String[]{"Berta"}, RunsProcessor.parseStringList("Berta"));
        assertArrayEquals(new String[]{"Berta"}, RunsProcessor.parseStringList("„Berta“"));
        assertArrayEquals(new String[]{"Berta", "Horst"}, RunsProcessor.parseStringList("„Berta“, \"Horst\""));
        assertArrayEquals(new String[]{"Berta", "Horst"}, RunsProcessor.parseStringList("Berta, Horst"));
        assertArrayEquals(new String[]{"Berta's", "Horst"}, RunsProcessor.parseStringList("„Berta's“, „Horst"));
        assertArrayEquals(new String[]{"A", "B", "", "D"}, RunsProcessor.parseStringList("„A“, „B\", '', D"));
        assertArrayEquals(new String[]{"A", "B", "", "D"}, RunsProcessor.parseStringList("„A“, „B\"  '' D"));
        assertArrayEquals(new String[]{"A', „B", "", "D"}, RunsProcessor.parseStringList("„A', „B\"  '' D"));
    }

    @Test
    public void regexpTest() {
        assertMatcher("{if Arbeitszeit = „Teilzeit“}", "Arbeitszeit", "=", "Teilzeit");
        assertMatcher("{if Arbeitszeit = ‚Teilzeit‘}", "Arbeitszeit", "=", "Teilzeit");
        assertMatcher("{if Arbeitszeit != ‚Vollzeit‘}", "Arbeitszeit", "!=", "Vollzeit");
        assertMatcher("{if Arbeitszeit in ‚Vollzeit‘}", "Arbeitszeit", "in", "Vollzeit");
        assertMatcher("{if Arbeitszeit !in ‚Vollzeit‘}", "Arbeitszeit", "!in", "Vollzeit");
        assertMatcher("{if Arbeitszeit !in ‚Vollzeit\"}", "Arbeitszeit", "!in", "Vollzeit\"");
        assertMatcher("{if name = „Horst's“}", "name", "=", "Horst's");
    }

    private void assertMatcher(String str, String... groups) {
        Matcher matcher = RunsProcessor.beginIfPattern.matcher(str);
        assertEquals(groups.length > 0 ? true : false, matcher.find());
        if (groups.length == 0) {
            return;
        }
        assertEquals(3, matcher.groupCount(), "Number of regexp group count.");
        assertEquals(groups[0], matcher.group(1));
        assertEquals(groups[1], matcher.group(2));
        String[] params = RunsProcessor.parseStringList(matcher.group(3));
        assertEquals(groups.length - 2, params.length, "Number of comma separated values.");
        for (int i = 2; i < groups.length; i++) {
            assertEquals(groups[i], params[i - 2]);
        }
    }
}
