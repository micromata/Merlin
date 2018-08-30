package de.reinhard.merlin.word;

import de.reinhard.merlin.Definitions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordDocumentTest {
    private Logger log = LoggerFactory.getLogger(WordDocumentTest.class);

    @Test
    public void readWordTest() throws Exception {
        Map<String, String> variables = new HashMap<>();
        variables.put("Mitarbeiter", "Kai Reinhard");
        variables.put("Anrede", "Herr");
        variables.put("Datum", "1.1.2001");
        variables.put("Wochenstunden", "30");
        WordDocument document = new WordDocument(new File(Definitions.EXAMPLES_TEST_DIR, "Vertrag.docx"));
        document.process(variables);
        XWPFDocument doc = document.getDocument();
        File file = new File(Definitions.OUTPUT_DIR, "Vertrag.docx");
        log.info("Writing modified MS Word file: " + file.getAbsolutePath());
        doc.write(new FileOutputStream(file));
    }

    @Test
    public void regExpTest() {
        Map<String, String> variables = new HashMap<>();
        variables.put("var", "world");
        variables.put("endlessLoop", "${endlessLoop}");
        WordDocument word = processDocument(variables);
        XWPFDocument doc = word.getDocument();
    }

    private WordDocument processDocument(Map<String, String> variables) {
        XWPFDocument doc = new XWPFDocument();
        List<Entry> exptected = new LinkedList<>();
        add(exptected, createParagraph(doc, "Hello ${var}."),
                new String[]{"Hello world."});
        add(exptected, createParagraph(doc, "Hello ${var}"),
                new String[]{"Hello world"});
        add(exptected, createParagraph(doc, "${var}"),
                new String[]{"world"});
        add(exptected, createParagraph(doc, "Hello ", "$", "{var}"),
                new String[]{"Hello ", "world", ""});
        add(exptected, createParagraph(doc, ""),
                new String[]{""});
        add(exptected, createParagraph(doc, "", null, "Hello ${", "var}"),
                new String[]{"", null, "Hello world", ""});
        add(exptected, createParagraph(doc, "", null, "Hello ", "$", "{", "v", "ar", "}", "test"),
                new String[]{"", null, "Hello ", "world", "", "", "", "", "test"});
        add(exptected, createParagraph(doc, "", null, "Hello ${", "var}. What about ${name}?"),
                new String[]{"", null, "Hello world", ". What about ${name}?"});
        add(exptected, createParagraph(doc, "Hello ${var}. What about $", "{name}?"),
                new String[]{"Hello world. What about $", "{name}?"});
        add(exptected, createParagraph(doc, "${name}", "Hello ${var}."),
                new String[]{"${name}", "Hello world."});
        add(exptected, createParagraph(doc, "$", "{name}", "Hello ${var}."),
                new String[]{"$", "{name}", "Hello world."});
        add(exptected, createParagraph(doc, "Endless loop test: ${endlessLoop}."),
                new String[]{"Endless loop test: _{endlessLoop}."});
        WordDocument word = new WordDocument(doc);
        word.process(variables);
        int no = 0;
        for (Entry entry : exptected) {
            assertRuns(entry.par, no++, entry.expected);
        }
        /*
        for (XWPFParagraph par : doc.getParagraphs()) {
            log.debug("Paragraph: " + par.getText());
            for (XWPFRun run : par.getRuns()) {
                log.debug(" Run: " + run.getText(0));}
        }*/
        return word;
    }

    private XWPFParagraph createParagraph(XWPFDocument document, String... runs) {
        XWPFParagraph paragraph = document.createParagraph();
        if (runs == null) {
            return paragraph;
        }
        for (String run : runs) {
            paragraph.createRun().setText(run);
        }
        return paragraph;
    }

    private void assertRuns(XWPFParagraph paragraph, int no, String... expected) {
        List<XWPFRun> runs = paragraph.getRuns();
        assertEquals(expected.length, runs.size(), "Number of runs for " + no + ": " + String.join("|", expected));
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], runs.get(i).getText(0), "Run text for " + no + " " + String.join(",", expected));
        }
    }

    private void add(List<Entry> list, XWPFParagraph par, String... expected) {
        list.add(new Entry(par, expected));
    }

    private class Entry {
        XWPFParagraph par;
        String[] expected;

        Entry(XWPFParagraph par, String... expected) {
            this.par = par;
            this.expected = expected;
        }
    }
}
