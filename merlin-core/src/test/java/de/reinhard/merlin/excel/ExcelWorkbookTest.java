package de.reinhard.merlin.excel;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.I18n;
import de.reinhard.merlin.data.PropertiesStorage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExcelWorkbookTest {
    private Logger log = LoggerFactory.getLogger(ExcelWorkbookTest.class);

    static {
        I18n.setDefault(Locale.ROOT);
    }

    @Test
    public void configReaderValidationTest() {
        I18n i18n = I18n.getDefault();
        ExcelWorkbook excelWorkbook = new ExcelWorkbook(new File(Definitions.EXAMPLES_TEST_DIR, "Test.xlsx"));
        ExcelConfigReader configReader = new ExcelConfigReader(excelWorkbook.getSheet("Config"),
                "Property", "Value");
        PropertiesStorage props = configReader.readConfig(excelWorkbook);
        assertTrue(configReader.getSheet().hasValidationErrors());
        Set<ExcelValidationErrorMessage> validationErrors = configReader.getSheet().getAllValidationErrors();
        assertEquals(1, validationErrors.size());
        assertEquals("In sheet 'Config', column A:'Property' and row #5: Cell value 'user' isn't unique. It's already used in row #3.",
                validationErrors.iterator().next().getMessageWithAllDetails(i18n));
        assertEquals("horst", props.getConfig("user"));
        assertEquals("Hamburg", props.getConfig("city"));
    }

    @Test
    public void validationExcelResponseTest() throws IOException {
        ExcelWorkbook excelWorkbook = new ExcelWorkbook("examples/tests/Test.xlsx");
        ExcelConfigReader configReader = new ExcelConfigReader(excelWorkbook.getSheet("Config"),
                "Property", "Value");
        PropertiesStorage props = configReader.readConfig(excelWorkbook);
        assertTrue(configReader.getSheet().hasValidationErrors());
        ExcelWriterContext ctx = new ExcelWriterContext(I18n.getDefault(), excelWorkbook).setAddErrorColumn(true);
        configReader.getSheet().markErrors(ctx);

        ExcelSheet sheet = excelWorkbook.getSheet("Validator-Test");
        sheet.add("Name", new ExcelColumnValidator().setUnique());
        sheet.add("E-Mail", new ExcelColumnValidator().setRequired());
        sheet.markErrors(ctx);

        File file = new File(Definitions.OUTPUT_DIR, "Test-result.xlsx");
        log.info("Writing modified Excel file: " + file.getAbsolutePath());
        excelWorkbook.getPOIWorkbook().write(new FileOutputStream(file));
    }
}
