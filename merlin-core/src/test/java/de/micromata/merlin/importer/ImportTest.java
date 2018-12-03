package de.micromata.merlin.importer;

import de.micromata.merlin.Definitions;
import de.micromata.merlin.excel.*;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImportTest {
    @Test
    void importTest() {
        Map<String, Voter> db = new HashMap<>();
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
        ImportSet<Voter> set = new ImportSet<>() {
            @Override
            public Voter getAlreadyPersistedEntry(ImportDataEntry<Voter> entry) {
                return db.get(entry.getPrimaryKey());
            }
        };
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
            set.add(voter, voter.number);
        }
        ImportStatistics stats = set.getStatistics();
        assertEquals(8, stats.getNumberOfFaultyElements(), "Number of elements with multiple pks.");
        assertEquals(100, stats.getTotalNumberOfElements());
        assertEquals(100, stats.getNumberOfNotReconciledElements());

        set.reconcile();
        assertEquals(8, stats.getNumberOfFaultyElements(), "Number of elements with multiple pks.");
        assertEquals(100, stats.getTotalNumberOfElements());
        assertEquals(8, stats.getNumberOfNotReconciledElements());
        assertEquals(92, stats.getNumberOfNewElements());
    }

    class Voter {
        String surname, name, email, street, zipCode, city, birthday, number;
    }
}
