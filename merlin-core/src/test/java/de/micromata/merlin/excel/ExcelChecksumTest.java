package de.micromata.merlin.excel;

import de.micromata.merlin.Definitions;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ExcelChecksumTest {
    private Logger log = LoggerFactory.getLogger(ExcelChecksumTest.class);

    @Test
    void xssfChecksumTest() throws FileNotFoundException, IOException {
        Workbook poiWorkbook = new XSSFWorkbook();
        ExcelWorkbook workbook = new ExcelWorkbook(poiWorkbook);
        createAndCheck(workbook);
    }

    @Test
    void hssfChecksumTest() throws FileNotFoundException, IOException {
        Workbook poiWorkbook = new HSSFWorkbook();
        ExcelWorkbook workbook = new ExcelWorkbook(poiWorkbook);
        createAndCheck(workbook);
    }

    private void createAndCheck(ExcelWorkbook workbook) throws IOException {
        ExcelSheet sheet = workbook.createOrGetSheet("Sheet 1");
        ExcelRow row = sheet.createRow();
        row.createCells("cell 1", "cell 2");
        long checksum = ExcelChecksum.buildChecksum(workbook.getPOIWorkbook());
        assertEquals(-1, ExcelChecksum.readChecksum(workbook.getPOIWorkbook()));
        ExcelChecksum.writeChecksum(workbook.getPOIWorkbook(), checksum);
        assertEquals(checksum, ExcelChecksum.readChecksum(workbook.getPOIWorkbook()));

        String fileSuffix = workbook.getPOIWorkbook() instanceof XSSFWorkbook ? "xlsx" : "xls";
        File file = new File(Definitions.OUTPUT_DIR, "Checksum." + fileSuffix);
        log.info("Writing checksum Excel file: " + file.getAbsolutePath());
        workbook.getPOIWorkbook().write(new FileOutputStream(file));

        workbook = new ExcelWorkbook(file);
        assertEquals(checksum, ExcelChecksum.readChecksum(workbook.getPOIWorkbook()));
        workbook.createOrGetSheet("Sheet 2");
        long newChecksum = ExcelChecksum.buildChecksum(workbook.getPOIWorkbook());
        assertNotEquals(checksum, newChecksum);
        checksum = newChecksum;
        Cell cell = workbook.getSheet("Sheet 1").getRow(0).getRow().getCell(0);
        cell.setCellValue("New value");
        assertNotEquals(checksum, newChecksum = ExcelChecksum.buildChecksum(workbook.getPOIWorkbook()));
        checksum = newChecksum;
        workbook.createOrGetSheet("Sheet 2").createRow().createCells("test");
        assertNotEquals(checksum, newChecksum = ExcelChecksum.buildChecksum(workbook.getPOIWorkbook()));
    }
}
