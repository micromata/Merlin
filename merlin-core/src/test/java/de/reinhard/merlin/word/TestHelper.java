package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;

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
        assertEquals(expected.length, runs.size(), "Number of runs for " + no + ": " + String.join("|", expected));
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], runs.get(i).getText(0), "Run text for " + no + " " + String.join(",", expected));
        }
    }
}
