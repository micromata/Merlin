package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.persistency.FileDescriptor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class SerialDataExcelTest {
    private Logger log = LoggerFactory.getLogger(SerialDataExcelTest.class);
    private TemplateRunContext templateRunContext = new TemplateRunContext();

    @Test
    public void writeReadExcelTest() throws Exception {
        TemplateDefinition templateDefinition = DefinitionExcelConverterTest.create();
        Template template = new Template();
        template.setFileDescriptor(new FileDescriptor());
        template.getFileDescriptor().setDirectory(Definitions.OUTPUT_DIR.toPath()).setRelativePath(".").setFilename("ContractTemplate.docx");
        SerialData origSerialData = createSerialData(template);
        template.assignTemplateDefinition(templateDefinition);
        origSerialData.setTemplate(template);
        SerialDataExcelWriter writer = new SerialDataExcelWriter(origSerialData);
        ExcelWorkbook workbook = writer.writeToWorkbook();
        File file = new File(Definitions.OUTPUT_DIR, "ContractSerialData.xlsx");
        log.info("Writing modified Excel file: " + file.getAbsolutePath());
        workbook.getPOIWorkbook().write(new FileOutputStream(file));

        workbook = new ExcelWorkbook(file);
        assertTrue(SerialDataExcelReader.isMerlinSerialRunDefinition(workbook));
        SerialDataExcelReader reader = new SerialDataExcelReader(workbook);
        SerialData serialData = reader.getSerialData();
        reader.readVariables(template.getStatistics());
        // assertEquals(template.getFileDescriptor().getPrimaryKey(), serialData.getTemplate().getPrimaryKey());
        assertEquals(origSerialData.getFilenamePattern(), serialData.getFilenamePattern());
        // assertEquals(templateDefinition.getId(), serialData.getTemplateDefinition().getPrimaryKey());
        assertEquals(origSerialData.getEntries().size(), serialData.getEntries().size());
        for (int i = 0; i < origSerialData.getEntries().size(); i++) {
            Map<String, Object> origMap = origSerialData.getEntries().get(i).getVariables();
            Map<String, Object> map = serialData.getEntries().get(i).getVariables();
            assertEquals(origMap.size(), map.size());
            for (Map.Entry<String, Object> entry : origMap.entrySet()) {
                assertNotNull(map.get(entry.getKey()));
                assertEquals(entry.getValue(), map.get(entry.getKey()));
            }
        }
    }

    SerialData createSerialData(Template template) {
        SerialData serialData = new SerialData();
        serialData.add(createEntry("female", "Berta Smith", "09/14/2018", "01/01/2008", 40, 30));
        serialData.add(createEntry("male", "Kai Reinhard", "09/14/2018", "08/01/2001", 30, 30));
        serialData.setFilenamePattern("contract-${Employee}");
        List<String> usedVariables = new ArrayList<>();
        addAll(usedVariables, "Gender", "Employee", "Date", "BeginDate", "WeeklyHours", "NumberOfLeaveDays");
        template.getStatistics().setUsedVariables(usedVariables);
        return serialData;
    }

    SerialDataEntry createEntry(String gender, String employee, String date, String beginDate, int weeklyHours, int numberOfLeaveDays) {
        SerialDataEntry entry = new SerialDataEntry();
        entry.put("Gender", gender);
        entry.put("Employee", employee);
        try {
            entry.put("Date", templateRunContext.getDateFormatter().parse(date));
            entry.put("BeginDate", templateRunContext.getDateFormatter().parse(beginDate));
        } catch (ParseException ex) {
            fail("Couldn't parse date: " + ex.getMessage());
        }
        entry.put("WeeklyHours", weeklyHours);
        entry.put("NumberOfLeaveDays", numberOfLeaveDays);
        return entry;
    }

    private void addAll(List<String> list, String... values) {
        for (String value : values) {
            list.add(value);
        }
    }
}
