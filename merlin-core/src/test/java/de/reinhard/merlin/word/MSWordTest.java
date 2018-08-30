package de.reinhard.merlin.word;

import de.reinhard.merlin.Definitions;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ranges.Range;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSWordTest {
    private Logger log = LoggerFactory.getLogger(MSWordTest.class);

    @Test
    public void readWordTest() throws Exception {
        Map<String, String> variables = new HashMap<>();
        variables.put("Mitarbeiter", "Kai Reinhard");
        variables.put("Anrede", "Herr");
        variables.put("Datum", "1.1.2001");
        variables.put("Wochenstunden", "30");
        MSWord document = new MSWord(new File(Definitions.EXAMPLES_TEST_DIR, "Vertrag.docx"));
        document.process(variables);
        XWPFDocument doc = document.getDocument();
        File file = new File(Definitions.OUTPUT_DIR, "Vertrag.docx");
        log.info("Writing modified MS Word file: " + file.getAbsolutePath());
        doc.write(new FileOutputStream(file));
    }
}
