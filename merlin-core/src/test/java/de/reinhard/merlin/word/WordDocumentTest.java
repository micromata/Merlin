package de.reinhard.merlin.word;

import de.reinhard.merlin.Definitions;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

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
    public void patternTest() {
        assertVariablePatternMatch(true, "${var}");
        assertVariablePatternMatch(true, "${ var  }");
        assertVariablePatternMatch(true, "..   ${ var}..");
        assertVariablePatternMatch(true, "${var2}");
        assertVariablePatternMatch(true, "${_Var_3kl}");
        assertVariablePatternMatch(false, "${3_Var_3kl}");
        assertVariablePatternMatch(false, "$ { var  }");

        assertBeginIfPatternMatch(true, "{if var='value'} ...", "var", "=", "'value'");
        assertBeginIfPatternMatch(true, "ksfl {if var!='value'} ...", "var", "!=", "'value'");
        assertBeginIfPatternMatch(true, "... {if _var5 != ‚23 value'} ...", "_var5", "!=", "‚23 value'");
        assertBeginIfPatternMatch(true, "{if  var != „value 2839'  }", "var", "!=", "„value 2839'  ");
        assertBeginIfPatternMatch(false, "ksfl {ifvar != 'value'  } ...", "var", "!=", "'value'  ");
    }

    private void assertBeginIfPatternMatch(boolean expected, String str, String var, String cmp, String value) {
        Matcher matcher = Conditional.beginIfPattern.matcher(str);
        assertEquals(expected, matcher.find());
        if (expected) {
            assertEquals(var, matcher.group(1), "Variable.");
            assertEquals(cmp, matcher.group(2), "Comparison.");
            assertEquals(value, matcher.group(3), "Value.");
        }
    }

    private void assertVariablePatternMatch(boolean expected, String str) {
        Matcher matcher = RunsProcessor.defaultVariablePattern.matcher(str);
        assertEquals(expected, matcher.find());
    }

    @Test
    public void regExpTest() {
        Map<String, String> variables = new HashMap<>();
        variables.put("var", "world");
        variables.put("endlessLoop", "${endlessLoop}");
        variables.put("endlessLoop2", "... ${endlessLoop} ...");
        variables.put("endlessLoop3", "....................................... ${endlessLoop} ...");
        WordDocument word = processDocument(variables);
        XWPFDocument doc = word.getDocument();
    }

    private WordDocument processDocument(Map<String, String> variables) {
        XWPFDocument doc = new XWPFDocument();
        List<Entry> exptected = new LinkedList<>();
        add(exptected, TestHelper.createParagraph(doc, "Hello ${var}."),
                new String[]{"Hello world."});
        add(exptected, TestHelper.createParagraph(doc, "Hello ${var}"),
                new String[]{"Hello world"});
        add(exptected, TestHelper.createParagraph(doc, "${var}"),
                new String[]{"world"});
        add(exptected, TestHelper.createParagraph(doc, "Hello ", "$", "{var}"),
                new String[]{"Hello ", "world", ""});
        add(exptected, TestHelper.createParagraph(doc, ""),
                new String[]{""});
        add(exptected, TestHelper.createParagraph(doc, "", null, "Hello ${", "var}"),
                new String[]{"", null, "Hello world", ""});
        add(exptected, TestHelper.createParagraph(doc, "", null, "Hello ", "$", "{", "v", "ar", "}", "test"),
                new String[]{"", null, "Hello ", "world", "", "", "", "", "test"});
        add(exptected, TestHelper.createParagraph(doc, "", null, "Hello ${", "var}. What about ${name}?"),
                new String[]{"", null, "Hello world", ". What about ${name}?"});
        add(exptected, TestHelper.createParagraph(doc, "Hello ${var}. What about $", "{name}?"),
                new String[]{"Hello world. What about $", "{name}?"});
        add(exptected, TestHelper.createParagraph(doc, "${name}", "Hello ${var}."),
                new String[]{"${name}", "Hello world."});
        add(exptected, TestHelper.createParagraph(doc, "$", "{name}", "Hello ${var}."),
                new String[]{"$", "{name}", "Hello world."});
        add(exptected, TestHelper.createParagraph(doc, "Endless loop test: ${endlessLoop}."),
                new String[]{"Endless loop test: ${endlessLoop}."});
        add(exptected, TestHelper.createParagraph(doc, "Endless loop test 2: ${endlessLoop2}."),
                new String[]{"Endless loop test 2: ... ${endlessLoop} ...."});
        add(exptected, TestHelper.createParagraph(doc, "Endless loop test 3: ${endlessLoop3}."),
                new String[]{"Endless loop test 3: ....................................... ${endlessLoop} ...."});
        add(exptected, TestHelper.createParagraph(doc, "Endless loop test 3: ${end", "lessLoop3}."),
                new String[]{"Endless loop test 3: ....................................... ${endlessLoop} ...", "."});
        WordDocument word = new WordDocument(doc);
        word.process(variables);
        int no = 0;
        for (Entry entry : exptected) {
            TestHelper.assertRuns(entry.par, no++, entry.expected);
        }
        /*
        for (XWPFParagraph par : doc.getParagraphs()) {
            log.debug("Paragraph: " + par.getText());
            for (XWPFRun run : par.getRuns()) {
                log.debug(" Run: " + run.getText(0));}
        }*/
        return word;
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
