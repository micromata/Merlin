package de.reinhard.merlin.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExcelWorkbook {
    private Logger log = LoggerFactory.getLogger(ExcelWorkbook.class);

    private Workbook workbook;
    private List<ExcelSheet> sheetList;
    private Map<String, CellStyle> cellStyleMap = new HashMap<>();
    private Map<String, Font> fontMap = new HashMap<>();

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
            ExcelSheet excelSheet = new ExcelSheet(this, poiSheet);
            sheetList.add(excelSheet);
        }
    }

    /**
     * @return true if any sheet of this workbook returns true: {@link ExcelSheet#isModified()}
     */
    public boolean isModified() {
        initializeSheetList();
        for (ExcelSheet sheet : sheetList) {
            if (sheet.isModified()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Please re-use cell styles due to limitations of Excel.
     * @param id
     * @return
     */
    public CellStyle createOrGetCellStyle(String id) {
        CellStyle cellStyle = cellStyleMap.get(id);
        if (cellStyle == null) {
            cellStyle = workbook.createCellStyle();
            cellStyleMap.put(id, cellStyle);
        }
        return cellStyle;
    }

    /**
     * Please re-use cell styles due to limitations of Excel.
     * @param id
     * @return
     */
    public Font createOrGetFont(String id) {
        Font font = fontMap.get(id);
        if (font == null) {
            font = workbook.createFont();
            fontMap.put(id, font);
        }
        return font;
    }
}
