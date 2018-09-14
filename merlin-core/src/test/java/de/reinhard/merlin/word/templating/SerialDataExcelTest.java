package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

public class SerialDataExcelTest {
    private Logger log = LoggerFactory.getLogger(SerialDataExcelTest.class);

    @Test
    public void writeReadExcelTest() throws Exception {
        TemplateDefinition templateDefinition = DefinitionExcelConverterTest.create();
        SerialDataExcelWriter writer = new SerialDataExcelWriter();
        SerialData serialData = createSerialData();
        ExcelWorkbook workbook = writer.writeToWorkbook(templateDefinition, serialData);
        File file = new File(Definitions.OUTPUT_DIR, "ContractSerialData.xlsx");
        log.info("Writing modified Excel file: " + file.getAbsolutePath());
        workbook.getPOIWorkbook().write(new FileOutputStream(file));

        workbook = new ExcelWorkbook(file);

    }

    SerialData createSerialData() {
        SerialData serialData = new SerialData();
        serialData.add(createEntry("female", "Berta Smith", "09/14/2018", "01/01/2008", 40, 30));
        serialData.add(createEntry("male", "Kai Reinhard", "09/14/2018", "08/01/2001", 30, 30));
        return serialData;
    }

    SerialDataEntry createEntry(String gender, String employee, String date, String beginDate, int weeklyHours, int numberOfLeaveDays) {
        SerialDataEntry entry = new SerialDataEntry();
        entry.put("Gender", gender);
        entry.put("Employee", employee);
        entry.put("Date", date);
        entry.put("BeginDate", beginDate);
        entry.put("WeeklyHours", weeklyHours);
        entry.put("NumberOfLeaveDays", numberOfLeaveDays);
        return entry;
    }
}
