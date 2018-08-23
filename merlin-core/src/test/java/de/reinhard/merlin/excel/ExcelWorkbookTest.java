package de.reinhard.merlin.excel;

import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.data.PropertiesStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

public class ExcelWorkbookTest {
    private ExcelWorkbook excelWorkbook;

    public ExcelWorkbookTest() {
        File outDir = new File("merlin-core/out");
        excelWorkbook = new ExcelWorkbook("examples/tests/Test.xlsx");
    }

    @Test
    public void configReaderValidationTest() {
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
}
