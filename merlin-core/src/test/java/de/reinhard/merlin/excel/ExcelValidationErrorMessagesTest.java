package de.reinhard.merlin.excel;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.I18n;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExcelValidationErrorMessagesTest {

    @Test
    public void validationErrorsTest() {
        I18n i18n = I18n.setDefault(Locale.ROOT);
        ExcelWorkbook excelWorkbook = new ExcelWorkbook(new File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Test.xlsx"));
        ExcelSheet sheet = excelWorkbook.getSheet("Config");
        ExcelColumnValidator val1 = new ExcelColumnValidator();
        val1.setColumnDef(new ExcelColumnDef(0, "cell-head1"));
        val1.setSheet(sheet);
        ExcelColumnValidator val2 = new ExcelColumnValidator();
        val2.setColumnDef(new ExcelColumnDef(303, "cell-head2"));
        val2.setSheet(sheet);
        ExcelColumnPatternValidator val3 = new ExcelColumnPatternValidator("yyyy-dd-mm");
        val3.setColumnDef(new ExcelColumnDef(303, "cell-head3"));
        val3.setSheet(sheet);
        assertEquals("In sheet 'Config', column A:'cell-head1' and row #6: Cell value not given but required.",
                val1.createValidationErrorRequired(5).getMessageWithAllDetails(i18n));
        assertEquals("In sheet 'Config', column KR:'cell-head2' and row #1: Cell value not given but required.",
                val2.createValidationErrorRequired(0).getMessageWithAllDetails(i18n));

        assertEquals("In sheet 'Config', column KR:'cell-head3' and row #6: Cell value doesn't match required pattern: 'cell-value' - 'yyyy-dd-mm'.",
                val3.createValidationErrorPatternMismatch(5, "cell-value", "yyyy-dd-mm").getMessageWithAllDetails(i18n));

        assertEquals("In sheet 'Config', column KR:'cell-head2' and row #6: Cell value isn't unique. It's already used in row #2: 'cell-value'.",
                val2.createValidationErrorUnique(5, "cell-value", 1).getMessageWithAllDetails(i18n));
        assertEquals("In column KR:'cell-head2': Cell value isn't unique. It's already used in row #2: 'cell-value'.",
                val2.createValidationErrorUnique(5, "cell-value", 1).getMessageWithColumn(i18n));
        assertEquals("Cell value isn't unique. It's already used in row #2: 'cell-value'.",
                val2.createValidationErrorUnique(5, "cell-value", 1).getMessage(i18n));

        assertEquals("In sheet 'Config': Named column not found: 'cell-head'.",
                sheet.createValidationErrorMissingColumnByName("cell-head").getMessageWithSheetName(i18n));
        assertEquals("In sheet 'Config': Column not given: KR.",
                sheet.createValidationErrorMissingColumnNumber(303).getMessageWithSheetName(i18n));
    }
}
