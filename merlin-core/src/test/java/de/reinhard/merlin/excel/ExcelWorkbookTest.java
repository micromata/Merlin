package de.reinhard.merlin.excel;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.data.PropertiesStorage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelWorkbookTest {

    @Test
    public void configReaderValidationTest() {
        ExcelWorkbook excelWorkbook = new ExcelWorkbook(new File(Definitions.EXAMPLES_TEST_DIR, "Test.xlsx"));
        ConfigReader configReader = new ConfigReader(excelWorkbook.getSheet("Config"),
                "Property", "Value");
        PropertiesStorage props = configReader.readConfig(excelWorkbook);
        assertTrue(configReader.hasValidationErrors());
        List<ResultMessage> validationErrors = configReader.getValidationErrors();
        assertEquals(1, validationErrors.size());
        assertEquals("Cell value 'user' isn't unique for column 'Property' in row no 4. It's already used in row number 2.",
                validationErrors.get(0).getMessage());
        assertEquals("horst", props.getConfig("user"));
        assertEquals("Hamburg", props.getConfig("city"));
    }

    @Test
    public void configReaderValidationExcelResponseTest() throws IOException {
        ExcelWorkbook excelWorkbook = new ExcelWorkbook("examples/tests/Test.xlsx");
        ConfigReader configReader = new ConfigReader(excelWorkbook.getSheet("Config"),
                "Property", "Value")
                .setMarkErrors(true);
        PropertiesStorage props = configReader.readConfig(excelWorkbook);
        assertTrue(configReader.hasValidationErrors());
        File file = new File(Definitions.OUTPUT_DIR, "Test-result.xlsx");
        excelWorkbook.getPOIWorkbook().write(new FileOutputStream(file));
    }
}
