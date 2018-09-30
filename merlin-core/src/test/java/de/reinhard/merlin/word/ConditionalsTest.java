package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionalsTest {
    private Logger log = LoggerFactory.getLogger(ConditionalsTest.class);

    @Test
    public void conditionalSimpleReadTest() {
        XWPFDocument doc = new XWPFDocument();
        TestHelper.createParagraph(doc, "First paragraph"); // 0
        TestHelper.createParagraph(doc, "{if var = 'test'}Hallo{endif}"); // 1
        TestHelper.createParagraph(doc, "{if var ! in ", "'test', 'blabla'}H", "allo{e", "ndif}"); // 2
        TestHelper.createParagraph(doc, "{if var < 5}H", "allo{e", "ndif}"); // 2
        Conditionals conditionals = new Conditionals(new WordDocument(doc));
        conditionals.read();
        assertEquals(3, conditionals.getConditionalsSet().size());
        Iterator<AbstractConditional> it = conditionals.getConditionalsSet().iterator();
        AbstractConditional cond = it.next();
        test(cond, "var", ConditionalType.EQUAL, 1, 1, "test");
        cond = it.next();
        test(cond, "var", ConditionalType.NOT_IN, 2, 2, "test", "blabla");
        cond = it.next();
        test(cond, "var", ConditionalType.LESS, 3, 3, 5);
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
        cond = it.next();
        test(cond, "fox", ConditionalType.EQUAL, 2, 3, "lazy");
        assertEquals(parent, cond.getParent());
        cond = it.next();
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
        for (String run:runs) {
            assertEquals(run, paragraph.getRuns().get(i).getText(0));
            i++;
        }
    }

    private void test(AbstractConditional conditional, String variable, ConditionalType type, int ifBodyElementNumber,
                      int endifBodyElementNumber, String... values) {
        assertEquals(variable, conditional.getVariable(), "Variable name.");
        assertEquals(type, conditional.getType(), "AbstractConditional type");
        assertArrayEquals(values, ((ConditionalString)conditional).getValues(), "Values");
        assertEquals(ifBodyElementNumber, conditional.getConditionalExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of if-statement.");
        assertEquals(endifBodyElementNumber, conditional.getEndConditionalExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of endif-statement");
    }

    private void test(AbstractConditional conditional, String variable, ConditionalType type, int ifBodyElementNumber,
                      int endifBodyElementNumber, double number) {
        assertEquals(variable, conditional.getVariable(), "Variable name.");
        assertEquals(type, conditional.getType(), "AbstractConditional type");
        assertTrue(ConditionalComparator.equals(number, ((ConditionalComparator)conditional).getDoubleValue()), "double value.");
        assertEquals(ifBodyElementNumber, conditional.getConditionalExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of if-statement.");
        assertEquals(endifBodyElementNumber, conditional.getEndConditionalExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of endif-statement");
    }
}
