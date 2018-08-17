package de.reinhard.merlin.excel;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExcelWorkbook {
    private static final Logger log = Logger.getLogger(ExcelWorkbook.class);

    private Workbook workbook;
    private List<ExcelSheet> sheetList;

    public ExcelWorkbook(String excelFilename) {
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

    public ExcelSheet getSheet(String sheetName) {
        if (sheetName == null) {
            log.error("Can't get sheet by name without given name. Name parameter is null.");
            return null;
        }
        for (ExcelSheet sheet : sheetList) {
            if (sheetName.equals(sheet.getSheetName())) {
                return sheet;
            }
        }
        log.warn("No sheet named '" + sheetName + "' found.");
        return null;
    }

    private void initializeSheetList() {
        if (sheetList != null) {
            return; // Already initialized.
        }
        sheetList = new LinkedList<>();
        for (Sheet poiSheet : workbook) {
            ExcelSheet excelSheet = new ExcelSheet(poiSheet);
            sheetList.add(excelSheet);
        }
    }

    /**
     * @return true if any sheet of this workbook returns true: {@link ExcelSheet#isMarkErrors()}
     */
    public boolean isMarkErrors() {
        initializeSheetList();
        for (ExcelSheet sheet : sheetList) {
            if (sheet.isMarkErrors()) {
                return true;
            }
        }
        return false;
    }
}
