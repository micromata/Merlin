package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ConditionalsTest {
    private Logger log = LoggerFactory.getLogger(ConditionalsTest.class);

    @Test
    public void conditionalSimpleReadTest() {
        XWPFDocument doc = new XWPFDocument();
        TestHelper.createParagraph(doc, "First paragraph"); // 0
        TestHelper.createParagraph(doc, "{if var = 'test'}Hallo{endif}"); // 1
        TestHelper.createParagraph(doc, "{if var ! in ", "'test', 'blabla'}H", "allo{e", "ndif}"); // 2
        Conditionals conditionals = new Conditionals(new WordDocument(doc));
        conditionals.read();
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
        TestHelper.createParagraph(doc, "Enjoy your life.{if fox in 'red', 'wild'}"); // 5
        TestHelper.createParagraph(doc, "Enjoy your life."); // 5
        TestHelper.createParagraph(doc, "Enjoy your life.{endif}"); // 5
        Conditionals conditionals = new Conditionals(new WordDocument(doc));
        conditionals.read();
        Iterator<Conditional> it = conditionals.getConditionals().iterator();
        Conditional cond = it.next();
        Conditional parent = cond;
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
        for (XWPFParagraph par:doc.getParagraphs()) {
            log.debug(par.getText());
        }
    }

    private void test(Conditional conditional, String variable, ConditionalType type, int ifBodyElementNumber,
                      int endifBodyElementNumber, String... values) {
        assertEquals(variable, conditional.getVariable(), "Variable name.");
        assertEquals(type, conditional.getType(), "Conditional type");
        assertArrayEquals(values, conditional.getValues(), "Values");
        assertEquals(ifBodyElementNumber, conditional.getIfExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of if-statement.");
        assertEquals(endifBodyElementNumber, conditional.getEndifExpressionRange().getStartPosition().getBodyElementNumber(), "body-number of endif-statement");
    }
}
