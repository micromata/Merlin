package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.word.WordDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WordTemplateRunnerTest {
    private Logger log = LoggerFactory.getLogger(WordTemplateRunnerTest.class);
    
    @Test
    public void readWordTest() throws Exception {
        TemplateDefinition templateDefinition = DefinitionExcelConverterTest.create();
        WordDocument templateDocument = new WordDocument(new File(Definitions.EXAMPLES_TEMPLATES_TEST_DIR, "EmploymentContractTemplate.docx"));
        WordTemplateRunner runner = new WordTemplateRunner(templateDefinition, templateDocument);
        String ref = runner.scanForTemplateDefinitionReference();
        assertEquals("Employment contract definition", ref);
        String id = runner.scanForTemplateId();
        assertNotNull(ref);
        assertEquals("Employment contract template", id);

        runDocument(runner, "Stéph Ciçá", "male", "3/16/2001",
                "4/1/2001", "25", "30");
        runDocument(runner, "Bärta Üßten", "female", "8/14/2017",
                "19/1/2017", "40", "30");
    }

    private void runDocument(WordTemplateRunner runner, String employee, String gender, String date,
                             String beginDate, String weeklyHours, String numberOfLeaveDays) throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("Employee", employee);
        variables.put("Gender", gender);
        variables.put("Vertragstyp", "befristet");
        variables.put("Vertragsende", "31.08.2017");
        variables.put("Date", date);
        variables.put("BeginDate", beginDate);
        variables.put("WeeklyHours", weeklyHours);
        variables.put("NumberOfLeaveDays", numberOfLeaveDays);
        runDocument(runner, variables);
    }

    private void runDocument(WordTemplateRunner runner, Map<String, Object> variables) throws Exception {
        String filename = runner.createFilename("file", variables);
        assertNotEquals("file", filename, "Filename pattern of template definition doesn't work, why?");
        WordDocument document = runner.run(variables);
        XWPFDocument doc = document.getDocument();
        File file = new File(Definitions.OUTPUT_DIR, filename);
        log.info("Writing modified MS Word file: " + file.getAbsolutePath());
        doc.write(new FileOutputStream(file));
    }
}
