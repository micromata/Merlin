package de.reinhard.merlin.excel;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.I18n;
import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.data.PropertiesStorage;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExcelValidationErrorMessagesTest {

    @Test
    public void validationErrorsTest() {
        I18n i18n = I18n.getInstance();
        ExcelWorkbook excelWorkbook = new ExcelWorkbook(new File(Definitions.EXAMPLES_TEST_DIR, "Test.xlsx"));
        ExcelSheet sheet = excelWorkbook.getSheet("Config");
        ColumnValidator val1 = new ColumnValidator();
        val1.setColumnDef(new ExcelColumnDef(0, "cell-head1"));
        val1.setSheet(sheet);
        ColumnValidator val2 = new ColumnValidator();
        val2.setColumnDef(new ExcelColumnDef(303, "cell-head2"));
        val2.setSheet(sheet);
        assertEquals("Cell value not given but required in sheet 'Config' for column A:'cell-head1' in row #6.",
                i18n.getMessage(val1.createValidationErrorRequired(5)));
        assertEquals("Cell value not given but required in sheet 'Config' for column KR:'cell-head2' in row #1.",
                i18n.getMessage(val2.createValidationErrorRequired(0)));

        assertEquals("Cell value 'cell-value' in sheet 'Config' for column KR:'cell-head2' in row #6 doesn't match required pattern 'yyyy-dd-mm'.",
                i18n.getMessage(val2.createValidationErrorPatternMismatch(5, "cell-value", "yyyy-dd-mm")));

        assertEquals("Cell value 'cell-value' in sheet 'Config' for column KR:'cell-head2' in row #6 isn't unique. It's already used in row #2.",
                i18n.getMessage(val2.createValidationErrorUnique(5, "cell-value", 1)));
    }
}
