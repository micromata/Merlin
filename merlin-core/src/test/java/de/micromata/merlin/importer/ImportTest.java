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
        Map<String, Person> db = new HashMap<>();
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
        ImportSet<Person> set = new ImportSet<Person>() {
            @Override
            public Person getAlreadyPersistedEntry(ImportDataEntry<Person> entry) {
                return db.get(entry.getPrimaryKey());
            }
        };
        while (it.hasNext()) {
            Row row = it.next();
            Person person = new Person();
            person.name = sheet.getCellString(row, "Name");
            person.surname = sheet.getCellString(row, "Surname");
            person.birthday = sheet.getCellString(row, "Birthday");
            person.street = sheet.getCellString(row, "Street");
            person.zipCode = sheet.getCellString(row, "Zip code");
            person.city = sheet.getCellString(row, "City");
            person.email = sheet.getCellString(row, "E-Mail");
            person.number = sheet.getCellString(row, "Number");
            set.add(person, person.number);
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

    class Person {
        String surname, name, email, street, zipCode, city, birthday, number;
    }
}
