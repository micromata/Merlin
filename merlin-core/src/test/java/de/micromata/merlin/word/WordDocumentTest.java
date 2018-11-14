package de.micromata.merlin.word;

import de.micromata.merlin.utils.ReplaceUtils;
import de.micromata.merlin.word.templating.Variables;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordDocumentTest {
    private Logger log = LoggerFactory.getLogger(WordDocumentTest.class);

    @Test
    void patternTest() {
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
        Matcher matcher = AbstractConditional.beginIfPattern.matcher(str);
        assertEquals(expected, matcher.find());
        if (expected) {
            assertEquals(var, matcher.group(2), "Variable.");
            assertEquals(cmp, matcher.group(3), "Comparison.");
            assertEquals(value, matcher.group(4), "Value.");
        }
    }

    private void assertVariablePatternMatch(boolean expected, String str) {
        Matcher matcher = ReplaceUtils.VARIABLE_PATTERN.matcher(str);
        assertEquals(expected, matcher.find());
    }

    @Test
    void regExpTest() {
        Variables variables = new Variables();
        variables.put("var", "world");
        variables.put("endlessLoop", "${endlessLoop}");
        variables.put("endlessLoop2", "... ${endlessLoop} ...");
        variables.put("endlessLoop3", "....................................... ${endlessLoop} ...");

        XWPFDocument doc = new XWPFDocument();
        List<WordDocumentTestEntry> exptected = new ArrayList<>();
        add(exptected, TestHelper.createParagraph(doc, "1: Hello ${var}."),
                new String[]{"1: Hello world."});
        add(exptected, TestHelper.createParagraph(doc, "2: Hello ${var}"),
                new String[]{"2: Hello world"});
        add(exptected, TestHelper.createParagraph(doc, "${var}"),
                new String[]{"world"});
        add(exptected, TestHelper.createParagraph(doc, "4: Hello ", "$", "{var}"),
                new String[]{"4: Hello ", "world"});
        add(exptected, TestHelper.createParagraph(doc, ""),
                new String[]{""});
        add(exptected, TestHelper.createParagraph(doc, "", null, "5: Hello ${", "var}"),
                new String[]{"", null, "5: Hello world"});
        add(exptected, TestHelper.createParagraph(doc, "", null, "6: Hello ", "$", "{", "v", "ar", "}", "test"),
                new String[]{"", null, "6: Hello ", "world", "test"});
        add(exptected, TestHelper.createParagraph(doc, "", null, "7: Hello ${", "var}. What about ${name}?"),
                new String[]{"", null, "7: Hello world", ". What about ${name}?"});
        add(exptected, TestHelper.createParagraph(doc, "8: Hello ${var}. What about $", "{name}?"),
                new String[]{"8: Hello world. What about $", "{name}?"});
        add(exptected, TestHelper.createParagraph(doc, "${name}", "9: Hello ${var}."),
                new String[]{"${name}", "9: Hello world."});
        add(exptected, TestHelper.createParagraph(doc, "$", "{name}", "10: Hello ${var}."),
                new String[]{"$", "{name}", "10: Hello world."});
        add(exptected, TestHelper.createParagraph(doc, "11: Endless loop test: ${endlessLoop}."),
                new String[]{"11: Endless loop test: ${endlessLoop}."});
        add(exptected, TestHelper.createParagraph(doc, "12: Endless loop test 2: ${endlessLoop2}."),
                new String[]{"12: Endless loop test 2: ... ${endlessLoop} ...."});
        add(exptected, TestHelper.createParagraph(doc, "13: Endless loop test 3: ${endlessLoop3}."),
                new String[]{"13: Endless loop test 3: ....................................... ${endlessLoop} ...."});
        add(exptected, TestHelper.createParagraph(doc, "14: Endless loop test 3: ${end", "lessLoop3}."),
                new String[]{"14: Endless loop test 3: ....................................... ${endlessLoop} ...", "."});
        WordDocument word = new WordDocument(doc);
        word.process(variables);
        int no = 0;
        for (WordDocumentTestEntry entry : exptected) {
            TestHelper.assertRuns(entry.par, no++, entry.expected);
        }
        /*
        for (XWPFParagraph par : doc.getParagraphs()) {
            log.debug("Paragraph: " + par.getText());
            for (XWPFRun run : par.getRuns()) {
                log.debug(" Run: " + run.getText(0));}
        }*/
    }

    @Test
    void conditionalsTest() {
        Variables variables = new Variables();
        variables.put("var", "world");
        variables.put("counter", "42");
        variables.put("endlessLoop2", "... ${endlessLoop} ...");
        variables.put("endlessLoop3", "....................................... ${endlessLoop} ...");

        XWPFDocument doc = new XWPFDocument();
        List<WordDocumentTestEntry> exptected = new ArrayList<>();
        add(exptected, TestHelper.createParagraph(doc, "1: Hello {if var = 'world'}Super world{endif}{if var != 'world'}${world}{endif}."),
                new String[]{"1: Hello Super world."});
        add(exptected, TestHelper.createParagraph(doc, "2: {if counter > 5}We have more than 5 elements: ${counter}{endif}"),
                new String[]{"2: We have more than 5 elements: 42"});
        add(exptected, TestHelper.createParagraph(doc, "3: {if counter > 5}Hello {if var = 'world'}Super world{endif}{if var != 'world'}${world}{endif}.{endif}"),
                new String[]{"3: Hello Super world."});
        add(exptected, TestHelper.createParagraph(doc, "4: {if counter <= 42}42 or less: ${counter}{endif}"),
                new String[]{"4: 42 or less: 42"});
        add(exptected, TestHelper.createParagraph(doc, "5: {if counter <= 41.99}---{endif}{if counter < 42}---{endif}{if counter > 42}---{endif}{if counter >= 42.1}---{endif}"),
                new String[]{"5: "});
        add(exptected, TestHelper.createParagraph(doc, "5: {if counter <= 42}${counter}<=42{endif}, {if counter < 42.5}${counter}<42.5{endif}, {if counter > 41.8}${counter}>41.8{endif}, {if counter >= 42}42>=42{endif}"
                + ", {if counter = 42}${counter}=42{endif}"),
                new String[]{"5: 42<=42, 42<42.5, 42>41.8, 42>=42, 42=42"});
        WordDocument word = new WordDocument(doc);
        word.process(variables);
        int no = 0;
        for (WordDocumentTestEntry entry : exptected) {
            TestHelper.assertRuns(entry.par, no++, entry.expected);
        }
        /*
        for (XWPFParagraph par : doc.getParagraphs()) {
            log.debug("Paragraph: " + par.getText());
            for (XWPFRun run : par.getRuns()) {
                log.debug(" Run: " + run.getText(0));}
        }*/
    }

    private void add(List<WordDocumentTestEntry> list, XWPFParagraph par, String... expected) {
        list.add(new WordDocumentTestEntry(par, expected));
    }
}
