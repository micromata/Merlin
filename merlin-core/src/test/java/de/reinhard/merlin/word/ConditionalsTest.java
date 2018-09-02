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
    public void conditionalReadTest() {
        XWPFDocument doc = new XWPFDocument();
        TestHelper.createParagraph(doc, "First paragraph");
        TestHelper.createParagraph(doc, "{if var = 'test'}Hallo{endif}");
        TestHelper.createParagraph(doc, "{if var = ", "'test'}H", "allo{e", "ndif}");
        Conditionals conditionals = new Conditionals();
        conditionals.read(doc.getBodyElements());
        assertEquals(2, conditionals.getConditionals().size());
        Iterator<Conditional> it = conditionals.getConditionals().iterator();
        Conditional cond = it.next();
        test(cond, "var", ConditionalType.EQUAL, "test");
    }

    private void test(Conditional conditional, String variable, ConditionalType type, String... values) {
        assertEquals(variable, conditional.getVariable());
        assertEquals(type, conditional.getType());
        assertArrayEquals(values, conditional.getValues());
    }
}
