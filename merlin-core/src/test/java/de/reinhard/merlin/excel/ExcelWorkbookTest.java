package de.reinhard.merlin.excel;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.I18n;
import de.reinhard.merlin.data.PropertiesStorage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExcelWorkbookTest {
    private Logger log = LoggerFactory.getLogger(ExcelWorkbookTest.class);
    static {
        I18n i18n = I18n.getInstance();
        i18n.setResourceBundle(ResourceBundle.getBundle("MessagesBundle", Locale.ROOT));
    }

    @Test
    public void configReaderValidationTest() {
        ExcelWorkbook excelWorkbook = new ExcelWorkbook(new File(Definitions.EXAMPLES_TEST_DIR, "Test.xlsx"));
        ConfigReader configReader = new ConfigReader(excelWorkbook.getSheet("Config"),
                "Property", "Value");
        PropertiesStorage props = configReader.readConfig(excelWorkbook);
        assertTrue(configReader.getSheet().hasValidationErrors());
        Set<ExcelValidationErrorMessage> validationErrors = configReader.getSheet().getAllValidationErrors();
        assertEquals(1, validationErrors.size());
        assertEquals("Cell value 'user' in sheet 'Config' for column A:'Property' in row #5 isn't unique. It's already used in row #3.",
                validationErrors.iterator().next().getMessage());
        assertEquals("horst", props.getConfig("user"));
        assertEquals("Hamburg", props.getConfig("city"));
    }

    @Test
    public void configReaderValidationExcelResponseTest() throws IOException {
        ExcelWorkbook excelWorkbook = new ExcelWorkbook("examples/tests/Test.xlsx");
        ConfigReader configReader = new ConfigReader(excelWorkbook.getSheet("Config"),
                "Property", "Value");
        PropertiesStorage props = configReader.readConfig(excelWorkbook);
        assertTrue(configReader.getSheet().hasValidationErrors());
        CellStyle cellStyle = excelWorkbook.getPOIWorkbook().createCellStyle();
        cellStyle.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
        configReader.getSheet().markErrors(true, cellStyle);
        File file = new File(Definitions.OUTPUT_DIR, "Test-result.xlsx");
        log.info("Writing modified Excel file: " + file.getAbsolutePath());
        excelWorkbook.getPOIWorkbook().write(new FileOutputStream(file));
    }
}
