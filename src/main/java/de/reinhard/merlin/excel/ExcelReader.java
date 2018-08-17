package de.reinhard.merlin.excel;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
            workbook = WorkbookFactory.create(excelFile);
        } catch (IOException ex) {
            log.error("Couldn't open File '" + excelFilename + "': " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        } catch (InvalidFormatException ex) {
            log.error("Unsupported file format '" + excelFilename + "': " + ex.getMessage(), ex);
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
