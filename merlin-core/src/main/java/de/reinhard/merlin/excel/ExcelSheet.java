package de.reinhard.merlin.excel;

import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.data.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExcelSheet {
    private Logger log = LoggerFactory.getLogger(ExcelSheet.class);

    private List<ExcelColumnDef> columnDefList = new LinkedList<>();
    private Sheet poiSheet;
    private Iterator<Row> rowIterator;
    private Row currentRow;
    private boolean markErrors;
    private final static int firstDataRow = 1; // 1st row (0) is head row.

    ExcelSheet(Sheet poiSheet) {
        log.info("Reading sheet '" + poiSheet.getSheetName() + "'");
        this.poiSheet = poiSheet;
        rowIterator = poiSheet.iterator();
        readHeadRow();
    }

    /**
     * Analyzes sheet.
     * <p>
     * Each cell will be analyzed by calling ColumnListener for each column with given
     * ColumnListener. If no Analyzer is set for a columng, the column cells will not be analyzed.
     *
     * @param validate if true, then each cell of a column with a given ColumnValidator will be validated.
     * @return this for chaining.
     */
    public ExcelSheet analyze(boolean validate) {
        for (Row row : poiSheet) {
            if (row.getRowNum() <= firstDataRow) {
                continue;
            }
            for (ExcelColumnDef columnDef : columnDefList) {
                if (!columnDef.hasColumnListeners()) {
                    continue;
                }
                for (ColumnListener listener : columnDef.getColumnListeners()) {
                    if (!(listener instanceof ColumnValidator) || validate) {
                        Cell cell = row.getCell(columnDef.getColumnNumber());
                        listener.readCell(cell, row.getRowNum());
                    }
                }
            }
        }
        return this;
    }

    /**
     * @param columnHeadname
     * @param listener
     * @return this for chaining.
     */
    public ExcelSheet add(String columnHeadname, ColumnListener listener) {
        ExcelColumnDef columnDef = getColumnDef(columnHeadname);
        if (columnDef == null) {
            log.error("Can't find column named '" + columnHeadname + "'. Column listener ignored.");
            if (isMarkErrors()) {
                // TODO: Add message to excel head row (new column).
            }
            return this;
        }
        return add(columnDef, listener);
    }

    /**
     * @param columnNumber
     * @param listener
     * @return this for chaining.
     */
    public ExcelSheet set(int columnNumber, ColumnValidator listener) {
        ExcelColumnDef columnDef = getColumnDef(columnNumber);
        if (columnDef == null) {
            log.error("Can't get column number " + columnNumber + ". Column validator ignored.");
            if (isMarkErrors()) {
                // TODO: Add message to excel head row (new column).
            }
            return this;
        }
        return add(columnDef, listener);
    }

    /**
     * @param columnDef
     * @param listener
     * @return this for chaining.
     */
    public ExcelSheet add(ExcelColumnDef columnDef, ColumnListener listener) {
        columnDef.addColumnListener(listener);
        return this;
    }

    public boolean hasNextRow() {
        return rowIterator.hasNext();
    }

    public void nextRow() {
        currentRow = rowIterator.next();
    }

    public void readRow(Data data) {
        for (ExcelColumnDef columnDef : columnDefList) {
            Cell cell = currentRow.getCell(columnDef.getColumnNumber());
            String value = PoiHelper.getValueAsString(cell);
            data.put(columnDef.getColumnHeadname(), value);
        }
    }

    /**
     * @param columnHeadname
     * @return
     */
    public String getCell(String columnHeadname) {
        ExcelColumnDef columnDef = getColumnDef(columnHeadname);
        if (columnDef == null) {
            log.warn("No entry named '" + columnHeadname + "' found in sheet '" + currentRow.getSheet().getSheetName() + "'. Checked also '" + columnHeadname.toLowerCase() + "'.");
            return null;
        }
        Cell cell = currentRow.getCell(columnDef.getColumnNumber());
        String value = PoiHelper.getValueAsString(cell);
        return value;
    }

    /**
     * @param columnDef
     * @return
     */
    public String getCell(ExcelColumnDef columnDef) {
        Cell cell = currentRow.getCell(columnDef.getColumnNumber());
        String value = PoiHelper.getValueAsString(cell);
        return value;
    }

    private void readHeadRow() {
        if (!rowIterator.hasNext()) {
            log.info("Sheet '" + poiSheet.getSheetName() + "' has now rows.");
            return;
        }
        log.info("Reading head row of sheet '" + poiSheet.getSheetName() + "'.");
        Row currentRow = rowIterator.next();
        int col = -1;
        for (Cell cell : currentRow) {
            ++col;
            String val = PoiHelper.getValueAsString(cell);
            log.debug("Reading head column '" + val + "' in column " + col);
            if (getColumnDef(val) != null) {
                log.warn("Duplicate column head: '" + val + "' in col #" + col);
            }
            if (val == null || val.length() == 0) {
                log.warn("Column head is empty for column " + col + " (" + col + ") in 1st row.");
                continue;
            }
            columnDefList.add(new ExcelColumnDef(col, StringUtils.defaultString(val)));
        }
    }

    public ExcelColumnDef getColumnDef(String columnHeadname) {
        if (StringUtils.isEmpty(columnHeadname)) {
            return null;
        }
        String lowerColumnHeadname = columnHeadname.toLowerCase();
        for (ExcelColumnDef columnDef : columnDefList) {
            if (lowerColumnHeadname.equals(columnDef.getColumnHeadname().toLowerCase())) {
                return columnDef;
            }
        }
        return null;
    }

    public ExcelColumnDef getColumnDef(int columnNumber) {
        for (ExcelColumnDef columnDef : columnDefList) {
            if (columnNumber == columnDef.getColumnNumber()) {
                return columnDef;
            }
        }
        return null;
    }

    public String getSheetName() {
        return poiSheet.getSheetName();
    }

    public boolean isMarkErrors() {
        return markErrors;
    }

    public boolean hasValidationErrors() {
        for (ExcelColumnDef columnDef : columnDefList) {
            if (columnDef.hasColumnListeners() == false) {
                continue;
            }
            for (ColumnListener columnListener : columnDef.getColumnListeners()) {
                if (!(columnListener instanceof ColumnValidator)) {
                    continue;
                }
                if (((ColumnValidator) columnListener).hasValidationErrors()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<ResultMessage> getValidationErrors() {
        List<ResultMessage> validationErrors = new LinkedList<>();
        for (ExcelColumnDef columnDef : columnDefList) {
            if (columnDef.hasColumnListeners() == false) {
                continue;
            }
            for (ColumnListener columnListener : columnDef.getColumnListeners()) {
                if (!(columnListener instanceof ColumnValidator)) {
                    continue;
                }
                ColumnValidator columnValidator =  (ColumnValidator) columnListener;
                if (columnValidator.hasValidationErrors()) {
                    validationErrors.addAll(columnValidator.getValidationErrors());
                }
            }
        }
        return validationErrors;
    }

    /**
     * @param markErrors If true, any validation errors in the Excel file will be marked and validation messages will be added to the workbook and saved.
     */
    public void setMarkErrors(boolean markErrors) {
        this.markErrors = markErrors;
    }
}
