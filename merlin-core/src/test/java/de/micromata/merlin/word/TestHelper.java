package de.micromata.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHelper {
    static XWPFParagraph createParagraph(XWPFDocument document, String... runs) {
        XWPFParagraph paragraph = document.createParagraph();
        if (runs == null) {
            return paragraph;
        }
        for (String run : runs) {
            paragraph.createRun().setText(run);
        }
        return paragraph;
    }

    static void assertRuns(XWPFParagraph paragraph, int no, String... expected) {
        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(expected.length, runs.size(), "Number of runs for " + no + ": expected=" + String.join("|", expected) + ", actual="
                + runs.stream().map(run -> run.getText(0))
                .collect(Collectors.joining("|")));
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], runs.get(i).getText(0), "Run text for " + no + " " + String.join(",", expected));
        }
    }
}
