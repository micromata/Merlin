package de.micromata.merlin.excel;

import de.micromata.merlin.CoreI18n;
import de.micromata.merlin.Definitions;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExcelWorkbookMassTest {
    private Logger log = LoggerFactory.getLogger(ExcelWorkbookMassTest.class);

    @Test
    void writeReadTest() throws IOException {
        CoreI18n coreI18N = CoreI18n.setDefault(Locale.ROOT);
        ExcelWorkbook xlsx = new ExcelWorkbook(new XSSFWorkbook());
        ExcelSheet sheet = xlsx.createOrGetSheet("Mass data");
        sheet.createRow().createCells("Name", "Street", "Zip code", "City", "Birthday", "Code");
        for (int i = 1; i <= 10000; i++) {
            sheet.createRow().createCells("Name " + i, "Street " + i, "Zip code " + i, "City " + i, "Birthday " + i, "Code " + i);
        }
        File file = new File(Definitions.OUTPUT_DIR, "Test-mass.xlsx");
        log.info("Writing modified Excel file: " + file.getAbsolutePath());
        try (FileOutputStream out = new FileOutputStream(file)) {
            xlsx.getPOIWorkbook().write(out);
        }
        xlsx = new ExcelWorkbook(file);
        sheet = xlsx.getSheet("Mass data");
        Iterator<Row> it = sheet.getPoiSheet().rowIterator();
        it.next(); // First row was header row.
        int i = 0;
        while (it.hasNext()) {
            ++i;
            Row row = it.next();
            assertEquals("Name " + i, row.getCell(0).getStringCellValue());
        }
        assertEquals(10000, i);
    }
}
