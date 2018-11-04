package de.micromata.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionalsTest {
    private Logger log = LoggerFactory.getLogger(ConditionalsTest.class);

    @Test
    public void conditionalsTest() {
        // Equals
        checkSingle("{if var = 'text...'}true{endif}");
        checkSingle("{if var = 'abc'}false{endif}");

        // Not equals
        checkSingle("{if var != 'abc'}true{endif}");
        checkSingle("{if var != 'text...'}false{endif}");

        // Exist
        checkSingle("{if var}true{endif}");
        checkSingle("{if emptyVar}false{endif}");
        checkSingle("{if blankVar}false{endif}");
        checkSingle("{if undefinied}false{endif}");

        // Not exist
        checkSingle("{if !var}false{endif}");
        checkSingle("{if !emptyVar}true{endif}");
        checkSingle("{if ! blankVar}true{endif}");
        checkSingle("{if  not undefinied}true{endif}");

        // >
        checkSingle("{if value > 41}true{endif}");
        checkSingle("{if !value > 41}false{endif}");
        checkSingle("{if value > 42}false{endif}");
        checkSingle("{if !value > 42}true{endif}");

        // >=
        checkSingle("{if value >= 41}true{endif}");
        checkSingle("{if !value >= 41}false{endif}");
        checkSingle("{if value >= 42}true{endif}");
        checkSingle("{if !value >= 42}false{endif}");
        checkSingle("{if value >= 43}false{endif}");
        checkSingle("{if !value >= 43}true{endif}");

        // <
        checkSingle("{if value < 43}true{endif}");
        checkSingle("{if !value < 43}false{endif}");
        checkSingle("{if value < 42}false{endif}");
        checkSingle("{if !value < 42}true{endif}");

        // <=
        checkSingle("{if value <= 43}true{endif}");
        checkSingle("{if !value <= 43}false{endif}");
        checkSingle("{if value <= 42}true{endif}");
        checkSingle("{if !value <= 42}false{endif}");
        checkSingle("{if value <= 41}false{endif}");
        checkSingle("{if !value <= 41}true{endif}");
    }

    private void checkSingle(String text) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("emptyVar", "");
        variables.put("blankVar", "  ");
        variables.put("var", "text...");
        variables.put("value", "42");
        XWPFDocument poiDoc = new XWPFDocument();
        TestHelper.createParagraph(poiDoc, text); // 0
        WordDocument doc = new WordDocument(poiDoc);
        doc.process(variables);
        List<XWPFParagraph> paragraphs = poiDoc.getParagraphs();
        if (text.contains("true")) {
            assertEquals(1, paragraphs.size());
            assertEquals("true", paragraphs.get(0).getText());
        } else {
            assertEquals(0, paragraphs.size());
        }
    }

    @Test
    public void conditionalStringMatchTest() {
        checkResult("lazy", "First paragraph",
                "The lazy fox jumps over the water.",
                "The lazy fox jumps over the water.");
    }

    private void checkResult(String lazyValue, String... expectedParagraphs) {
        XWPFDocument poiDoc = new XWPFDocument();
        TestHelper.createParagraph(poiDoc, "First paragraph"); // 0
        TestHelper.createParagraph(poiDoc, "The{if fox=”lazy”} lazy{endif} fox jumps over the water."); // 1
        TestHelper.createParagraph(poiDoc, "The{if fox in `lazy´, 'very lazy'} lazy{endif} fox jumps over the water."); // 1
        WordDocument doc = new WordDocument(poiDoc);
        Map<String, Object> variables = new HashMap<>();
        variables.put("fox", lazyValue);
        doc.process(variables);
        List<XWPFParagraph> paragraphs = poiDoc.getParagraphs();
        for (int i = 0; i < expectedParagraphs.length; i++) {
            assertEquals(expectedParagraphs[i], paragraphs.get(i).getText(), "Unexpected in paragraph #" + i);
        }
    }

    @Test
    public void conditionalSimpleReadTest() {
        XWPFDocument doc = new XWPFDocument();
        TestHelper.createParagraph(doc, "First paragraph"); // 0
        TestHelper.createParagraph(doc, "{if var = 'test'}Hallo{endif}"); // 1
        TestHelper.createParagraph(doc, "{if var ! in ", "'test', 'blabla'}H", "allo{e", "ndif}"); // 2
        TestHelper.createParagraph(doc, "{if var < 5}H", "allo{e", "ndif}"); // 3
        TestHelper.createParagraph(doc, "{if var}H", "allo{e", "ndif}"); // 3
        TestHelper.createParagraph(doc, "{if !var}H", "allo{e", "ndif}"); // 3
        Conditionals conditionals = new Conditionals(new WordDocument(doc));
        conditionals.read();
        assertEquals(5, conditionals.getConditionalsSet().size());
        Iterator<AbstractConditional> it = conditionals.getConditionalsSet().iterator();
        AbstractConditional cond = it.next();
        test(cond, "var", ConditionalType.EQUAL, 1, 1, "test");
        cond = it.next();
        test(cond, "var", ConditionalType.NOT_IN, 2, 2, "test", "blabla");
        cond = it.next();
        test(cond, "var", ConditionalType.LESS, 3, 3, 5);
        cond = it.next();
        test(cond, "var", ConditionalType.EXIST, 4, 4);
        cond = it.next();
        test(cond, "var", ConditionalType.EXIST, 5, 5);
    }

    @Test
    public void conditionalReadTest() {
        XWPFDocument doc = new XWPFDocument();
        TestHelper.createParagraph(doc, "First paragraph"); // 0
        TestHelper.createParagraph(doc, "{if var != 'test'}Headline"); // 1
        TestHelper.createParagraph(doc, "Is the lazy fox really lazy? {if fox = 'lazy'} Yes, he is."); // 2
        TestHelper.createParagraph(doc, "{endif}{if fox != 'lazy'} No, he isn't.{endif}"); // 3
        TestHelper.createParagraph(doc, "Now, everybody ", "knows the answer.{endif}"); // 4
        TestHelper.createParagraph(doc, "Enjoy your life.{if fox in 'red', 'wild'}"); // 5
        TestHelper.createParagraph(doc, "Enjoy your life."); // 5
        TestHelper.createParagraph(doc, "Enjoy your life.{endif}"); // 5
        Conditionals conditionals = new Conditionals(new WordDocument(doc));
        conditionals.read();
        Iterator<AbstractConditional> it = conditionals.getConditionalsSet().iterator();
        AbstractConditional cond = it.next();
        AbstractConditional parent = cond;
        test(cond, "var", ConditionalType.NOT_EQUAL, 1, 4, "test");
        assertNull(cond.getParent());

        Iterator<AbstractConditional> childIt = cond.getChildConditionals().iterator();
        cond = childIt.next();
        test(cond, "fox", ConditionalType.EQUAL, 2, 3, "lazy");
        assertEquals(parent, cond.getParent());
        cond = childIt.next();
        test(cond, "fox", ConditionalType.NOT_EQUAL, 3, 3, "lazy");
        assertEquals(parent, cond.getParent());

        Map<String, String> variables = new HashMap<>();
        variables.put("var", "not test");
        variables.put("fox", "lazy");
        conditionals.process(variables);
        Iterator<XWPFParagraph> pit = doc.getParagraphs().iterator();
        testRuns(pit.next(), "First paragraph");
        testRuns(pit.next(), "Headline");
        testRuns(pit.next(), "Is the lazy fox really lazy?  Yes, he is.");
        testRuns(pit.next(), "Now, everybody ", "knows the answer.");
        testRuns(pit.next(), "Enjoy your life.");
        assertFalse(pit.hasNext());
    }

    private void testRuns(XWPFParagraph paragraph, String... runs) {
        assertEquals(runs.length, paragraph.getRuns().size());
        int i = 0;
        for (String run : runs) {
            assertEquals(run, paragraph.getRuns().get(i).getText(0));
            i++;
        }
    }

    private void test(AbstractConditional conditional, String variable, ConditionalType type, int ifBodyElementNumber,
                      int endifBodyElementNumber, String... values) {
        assertEquals(variable, conditional.getVariable(), "Variable name.");
        assertEquals(type, conditional.getType(), "AbstractConditional type");
        assertArrayEquals(values, ((ConditionalString) conditional).getValues(), "Values");
        assertEquals(ifBodyElementNumber, conditional.getConditionalExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of if-statement.");
        assertEquals(endifBodyElementNumber, conditional.getEndConditionalExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of endif-statement");
    }

    private void test(AbstractConditional conditional, String variable, ConditionalType type, int ifBodyElementNumber,
                      int endifBodyElementNumber, double number) {
        assertEquals(variable, conditional.getVariable(), "Variable name.");
        assertEquals(type, conditional.getType(), "AbstractConditional type");
        assertTrue(ConditionalComparator.equalsEpsilon(number, ((ConditionalComparator) conditional).getDoubleValue()), "double value.");
        assertEquals(ifBodyElementNumber, conditional.getConditionalExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of if-statement.");
        assertEquals(endifBodyElementNumber, conditional.getEndConditionalExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of endif-statement");
    }
}
