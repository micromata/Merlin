package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionalsTest {
    private Logger log = LoggerFactory.getLogger(ConditionalsTest.class);

    @Test
    public void conditionalSimpleReadTest() {
        XWPFDocument doc = new XWPFDocument();
        TestHelper.createParagraph(doc, "First paragraph"); // 0
        TestHelper.createParagraph(doc, "{if var = 'test'}Hallo{endif}"); // 1
        TestHelper.createParagraph(doc, "{if var ! in ", "'test', 'blabla'}H", "allo{e", "ndif}"); // 2
        Conditionals conditionals = new Conditionals();
        conditionals.read(doc.getBodyElements());
        assertEquals(2, conditionals.getConditionals().size());
        Iterator<Conditional> it = conditionals.getConditionals().iterator();
        Conditional cond = it.next();
        test(cond, "var", ConditionalType.EQUAL, 1, 1, "test");
        cond = it.next();
        test(cond, "var", ConditionalType.EQUAL, 2, 2, "test", "blabla");
    }

    @Test
    public void conditionalReadTest() {
        XWPFDocument doc = new XWPFDocument();
        TestHelper.createParagraph(doc, "First paragraph"); // 0
        TestHelper.createParagraph(doc, "{if var != 'test'}Headline"); // 1
        TestHelper.createParagraph(doc, "Is the lazy fox really lazy? {if fox = 'lazy'} Yes, he is."); // 2
        TestHelper.createParagraph(doc, "{endif}{if fox != 'lazy'} No, he isn't.{endif}"); // 3
        TestHelper.createParagraph(doc, "Now, everybody knows the answer.{endif}"); // 4
        TestHelper.createParagraph(doc, "Enjoy your life."); // 5
        Conditionals conditionals = new Conditionals();
        conditionals.read(doc.getBodyElements());
        Iterator<Conditional> it = conditionals.getConditionals().iterator();
        Conditional cond = it.next();
        test(cond, "var", ConditionalType.NOT_EQUAL, 1, 4, "test");
        cond = it.next();
        test(cond, "fox", ConditionalType.EQUAL, 2, 3, "lazy");
        cond = it.next();
        test(cond, "fox", ConditionalType.NOT_EQUAL, 4, 4, "lazy");
    }

    private void test(Conditional conditional, String variable, ConditionalType type, int ifParNumber, int endifParNumber, String... values) {
        assertEquals(variable, conditional.getVariable(), "Variable name.");
        assertEquals(type, conditional.getType(), "Conditional type");
        assertArrayEquals(values, conditional.getValues(), "Values");
        assertEquals(ifParNumber, conditional.getIfExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of if-statement.");
        assertEquals(endifParNumber, conditional.getEndifExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of endif-statement");
    }
}
