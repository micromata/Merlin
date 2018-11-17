package de.micromata.merlin.excel;

import de.micromata.merlin.Definitions;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExcelChecksumTest {
    private Logger log = LoggerFactory.getLogger(ExcelChecksumTest.class);

    @Test
    void xssfChecksumTest() throws FileNotFoundException, IOException {
        Workbook poiWorkbook = new XSSFWorkbook();
        ExcelWorkbook workbook = new ExcelWorkbook(poiWorkbook);
        ExcelSheet sheet = workbook.createOrGetSheet("Sheet 1");
        ExcelRow row = sheet.createRow();
        row.createCells("cell 1", "cell 2");
        long checksum = ExcelChecksum.buildChecksum(workbook.getPOIWorkbook());
        assertEquals(-1, ExcelChecksum.readChecksum(workbook.getPOIWorkbook()));
        ExcelChecksum.writeChecksum(workbook.getPOIWorkbook(), checksum);
        assertEquals(checksum, ExcelChecksum.readChecksum(workbook.getPOIWorkbook()));
        File file = new File(Definitions.OUTPUT_DIR, "Checksum.xlsx");
        log.info("Writing checksum Excel file: " + file.getAbsolutePath());
        workbook.getPOIWorkbook().write(new FileOutputStream(file));
    }

    @Test
    void hssfChecksumTest() throws FileNotFoundException, IOException {
        Workbook poiWorkbook = new HSSFWorkbook();
        ExcelWorkbook workbook = new ExcelWorkbook(poiWorkbook);
        ExcelSheet sheet = workbook.createOrGetSheet("Sheet 1");
        ExcelRow row = sheet.createRow();
        row.createCells("cell 1", "cell 2");
        long checksum = ExcelChecksum.buildChecksum(workbook.getPOIWorkbook());
        assertEquals(-1, ExcelChecksum.readChecksum(workbook.getPOIWorkbook()));
        ExcelChecksum.writeChecksum(workbook.getPOIWorkbook(), checksum);
        assertEquals(checksum, ExcelChecksum.readChecksum(workbook.getPOIWorkbook()));
        File file = new File(Definitions.OUTPUT_DIR, "Checksum.xls");
        log.info("Writing checksum Excel file: " + file.getAbsolutePath());
        workbook.getPOIWorkbook().write(new FileOutputStream(file));
    }
}
