package de.reinhard.merlin.word;

import de.reinhard.merlin.Definitions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RunsParserTest {
    private Logger log = LoggerFactory.getLogger(RunsParserTest.class);

    @Test
    public void readWordTest() throws Exception {
        Map<String, String> variables = new HashMap<>();
        assertRunIdx(1,0,5, "12345", "${Hallo}");
        assertRunIdx(0,2,2, "12345", "${Hallo}");
        assertRunIdx(0,0,0, "$");
        assertRunIdx(-1,-1,0, "");
        assertRunIdx(0,0,0, "$", "{Mitarbeiter}");
        assertRunIdx(1,12,13, "$", "{Mitarbeiter}");
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
        RunsParser runsParser = new RunsParser(runs, null);
        runsParser.buildText();
        RunsParser.Position position = runsParser.getRunIdxAndPosition(pos);
        assertEquals(runIdx, position.runIndex, "Run index.");
        assertEquals(runIdxPos, position.runPos, "Run index position.");
    }
}
