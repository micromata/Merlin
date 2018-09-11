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

public class WordTemplateRunnerTest {
    private Logger log = LoggerFactory.getLogger(WordTemplateRunnerTest.class);

    @Test
    public void readWordTest() throws Exception {
        TemplateDefinition templateDefinition = DefinitionExcelConverterTest.create();
        WordDocument templateDocument = new WordDocument(new File(Definitions.EXAMPLES_TEST_DIR, "ContractTemplate.docx"));
        WordTemplateRunner runner = new WordTemplateRunner(templateDefinition, templateDocument);
        runDocument(runner, "kai", "Kai Reinhard", "male", "3/16/2001",
                "4/1/2001", "25", "30");
        runDocument(runner, "berta", "Berta Charlson", "female", "8/14/2017",
                "19/1/2017", "40", "30");
    }

    private void runDocument(WordTemplateRunner runner, String filenamepart, String employee, String gender, String date,
                             String beginDate, String weeklyHours, String numberOfLeaveDays) throws Exception {
        Map<String, String> variables = new HashMap<>();
        variables.put("Employee", employee);
        variables.put("Gender", gender);
        variables.put("Date", date);
        variables.put("BeginDate", beginDate);
        variables.put("WeeklyHours", weeklyHours);
        variables.put("NumberOfLeaveDays", numberOfLeaveDays);
        WordDocument document = runner.run(variables);
        XWPFDocument doc = document.getDocument();
        File file = new File(Definitions.OUTPUT_DIR, "Contract-" + filenamepart + ".docx");
        log.info("Writing modified MS Word file: " + file.getAbsolutePath());
        doc.write(new FileOutputStream(file));
    }
}
