package de.micromata.merlin.importer;

import de.micromata.merlin.Definitions;
import de.micromata.merlin.excel.*;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Iterator;

public class ImportTest {
    @Test
    void importTest() {
        ExcelWorkbook excelWorkbook = new ExcelWorkbook(new File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Test.xlsx"));
        ExcelSheet sheet = excelWorkbook.getSheet("Validator-Test");
        sheet.registerColumn("Name", new ExcelColumnValidator().setRequired());
        sheet.registerColumn("Surname", new ExcelColumnValidator().setRequired());
        sheet.registerColumn("Birthday", new ExcelColumnDateValidator());
        sheet.registerColumn("Street", new ExcelColumnValidator());
        sheet.registerColumn("Zip code", new ExcelColumnValidator());
        sheet.registerColumn("City", new ExcelColumnValidator());
        sheet.registerColumn("E-Mail", new ExcelColumnPatternValidator().setEMailPattern().setRequired().setUnique());
        sheet.registerColumn("Number", new ExcelColumnValidator().setUnique());

        Iterator<Row> it = sheet.getDataRowIterator();
        ImportSet<Voter> set = new ImportSet<>();
        while (it.hasNext()) {
            Row row = it.next();
            Voter voter = new Voter();
            voter.name = sheet.getCellString(row, "Name");
            voter.surname = sheet.getCellString(row, "Surname");
            voter.birthday = sheet.getCellString(row, "Birthday");
            voter.street = sheet.getCellString(row, "Street");
            voter.zipCode = sheet.getCellString(row, "Zip code");
            voter.city = sheet.getCellString(row, "City");
            voter.email = sheet.getCellString(row, "E-Mail");
            voter.number = sheet.getCellString(row, "Number");
            set.add(voter);
        }
    }

    class Voter {
        String surname, name, email, street, zipCode, city, birthday, number;
    }
}
