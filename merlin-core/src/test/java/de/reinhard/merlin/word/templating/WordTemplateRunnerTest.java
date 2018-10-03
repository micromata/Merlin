package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.word.WordDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WordTemplateRunnerTest {
    private Logger log = LoggerFactory.getLogger(WordTemplateRunnerTest.class);

    private final static String MY_WORKING_DIR = "/Users/kai/workspace/Micromata/MicromataV3/legalaffairs/Templates";

    @Test
    public void readWordTest() throws Exception {
        TemplateDefinition templateDefinition = DefinitionExcelConverterTest.create();
        WordDocument templateDocument = new WordDocument(new File(Definitions.EXAMPLES_TEMPLATES_TEST_DIR, "ContractTemplate.docx"));
        WordTemplateRunner runner = new WordTemplateRunner(templateDefinition, templateDocument);
        TemplateDefinitionReference ref = runner.scanForTemplateDefinitionReference();
        assertNotNull(ref);
        assertEquals("JZpnpojeSuN5JDqtm9KZ", ref.getTemplateDefinitionId());
        runDocument(runner, "kai", "Kai Reinhard", "male", "3/16/2001",
                "4/1/2001", "25", "30");
        runDocument(runner, "berta", "Berta Charlson", "female", "8/14/2017",
                "19/1/2017", "40", "30");
        runMyFiles();
    }

    private void runMyFiles() throws Exception {
        File dir = new File(MY_WORKING_DIR);
        if (!dir.exists()) {
            return;
        }
        log.info("Processing my-working-dir: " + MY_WORKING_DIR);
        WordDocument templateDocument = new WordDocument(new File(MY_WORKING_DIR, "Arbeitsvertrag-Test-Template.docx"));
        ExcelWorkbook workbook = new ExcelWorkbook(new File(MY_WORKING_DIR, "Arbeitsvertrag-Test-Template.xlsx"));
        TemplateDefinitionExcelReader reader = new TemplateDefinitionExcelReader();
        TemplateDefinition templateDefinition = reader.readFromWorkbook(workbook);
        WordTemplateRunner runner = new WordTemplateRunner(templateDefinition, templateDocument);
        Map<String, Object> variables = new HashMap<>();
        variables.put("Mitarbeiter", "Markus Meier");
        variables.put("Geschlecht", "männlich");
        variables.put("MA_Strasse", "ABC-Straße 5");
        variables.put("MA_PLZ", "12345");
        variables.put("MA_Ort", "Kassel");
        variables.put("Vertragstyp", "unbefristet");
        variables.put("Vertragsbeginn", "01.04.2017");
        variables.put("Vertragsende", "---");
        variables.put("Wochenstunden", "40");
        variables.put("Urlaubstage", "30");
        variables.put("Position", "Legalmitarbeiter");
        variables.put("Aufgaben", "- Rechtliche Fragen rund um lorem epsum\n- Rechtliche Belange im Allgemeinen\n- Rechtliches im Speziellen.");
        File file = new File(Definitions.OUTPUT_DIR, "Arbeitsvertrag-Markus.docx");
        runDocument(runner, file, variables);
        file = new File(Definitions.OUTPUT_DIR, "Arbeitsvertrag-Sandra.docx");
        variables.put("Mitarbeiter", "Sandra Schmidt");
        variables.put("Geschlecht", "weiblich");
        variables.put("Wochenstunden", "20");
        variables.put("Vertragstyp", "befristet");
        variables.put("Vertragsende", "31.08.2017");
        variables.put("Position", "Legalmitarbeiterin");
        runDocument(runner, file, variables);
    }

    private void runDocument(WordTemplateRunner runner, String filenamepart, String employee, String gender, String date,
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
        File file = new File(Definitions.OUTPUT_DIR, "Contract-" + filenamepart + ".docx");
        runDocument(runner, file, variables);
    }

    private void runDocument(WordTemplateRunner runner, File file, Map<String, Object> variables) throws Exception {
        WordDocument document = runner.run(variables);
        XWPFDocument doc = document.getDocument();
        log.info("Writing modified MS Word file: " + file.getAbsolutePath());
        doc.write(new FileOutputStream(file));
    }
}
