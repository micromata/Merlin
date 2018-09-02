package de.reinhard.merlin.word;

import de.reinhard.merlin.csv.CSVStringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

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
    }
}
