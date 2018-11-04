package de.micromata.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RunsProcessorTest {
    private Logger log = LoggerFactory.getLogger(RunsProcessorTest.class);

    @Test
    public void templateReferenceRegexTest() {
        assertMatcher("{template.name='letter'}", "name", "'letter'");
        assertMatcher("lfd lk { template.id= \"hruzer' }kdlfkaj", "id", "\"hruzer' ");
    }

    private void assertMatcher(String str, String variable, String quotedValue) {
        Matcher matcher = RunsProcessor.TEMPLATE_DEFINITION_REFERENCE_PATTERN.matcher(str);
        if (matcher.find()) {
            assertEquals(variable, matcher.group(1));
            assertEquals(quotedValue, matcher.group(2));
        }
    }

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

    static XWPFRun createRun(String text) {
        XWPFRun run = mock(XWPFRun.class);
        when(run.getText(0)).thenReturn(text);
        return run;
    }

    private void assertRunIdx(int runIdx, int runIdxPos, int pos, String... runStrings) {
        List<XWPFRun> runs = new ArrayList<>();
        for (String str : runStrings) {
            runs.add(createRun(str));
        }
        RunsProcessor runsParser = new RunsProcessor(runs);
        runsParser.getText();
        DocumentPosition position = runsParser.getRunIdxAndPosition(0, pos);
        if (runIdx == -1) {
            assertNull(position);
        } else {
            assertEquals(runIdx, position.getRunIndex(), "Run index.");
            assertEquals(runIdxPos, position.getRunCharAt(), "Run index position.");
        }
    }
}
