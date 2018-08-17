package de.reinhard.merlin.excel;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelReader {
    private static final Logger log = Logger.getLogger(ExcelReader.class);

    private Workbook workbook;

    public ExcelReader(String excelFilename) {
        FileInputStream excelFile;
        try {
            excelFile = new FileInputStream(new File(excelFilename));
        } catch (FileNotFoundException ex) {
            log.error("Couldn't open File '" + excelFilename + "': ", ex);
            throw new RuntimeException(ex);
        }
        try {
            workbook = new XSSFWorkbook(excelFile);
        } catch (IOException ex) {
            log.error("Couldn't open File '" + excelFilename + "': ", ex);
            throw new RuntimeException(ex);
        }
    }

    public ExcelSheetReader getSheet(String sheetName) {
        Sheet datatypeSheet = workbook.getSheet(sheetName);
        if (datatypeSheet == null) {
            log.warn("No sheet named '" + sheetName + "' found.");
            return null;
        }
        return new ExcelSheetReader(datatypeSheet);
    }
}
