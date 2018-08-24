package de.reinhard.merlin.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ExcelWorkbook {
    private Logger log = LoggerFactory.getLogger(ExcelWorkbook.class);

    private Workbook workbook;
    private List<ExcelSheet> sheetList;

    public ExcelWorkbook(String excelFilename) {
        this(new File(excelFilename));
    }

    public ExcelWorkbook(File excelFile) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(excelFile);
        } catch (FileNotFoundException ex) {
            log.error("Couldn't open File '" + excelFile.getAbsolutePath() + "': ", ex);
            throw new RuntimeException(ex);
        }
        try {
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException ex) {
            log.error("Couldn't open File '" + excelFile.getAbsolutePath() + "': " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        } catch (InvalidFormatException ex) {
            log.error("Unsupported file format '" + excelFile.getAbsolutePath() + "': " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }


    public ExcelSheet getSheet(String sheetName) {
        initializeSheetList();
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

    public Workbook getPOIWorkbook() {
        return workbook;
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
