package de.reinhard.merlin.excel;

import de.reinhard.merlin.data.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.util.*;

public class ExcelSheet {
    private static final Logger log = Logger.getLogger(ExcelSheet.class);

    private List<ExcelColumnDef> columnDefList = new LinkedList<>();
    private Sheet poiSheet;
    private Iterator<Row> rowIterator;
    private Row currentRow;
    boolean markErrors;

    public ExcelSheet(Sheet poiSheet) {
        log.info("Reading sheet '" + poiSheet.getSheetName() + "'");
        this.poiSheet = poiSheet;
        rowIterator = poiSheet.iterator();
        readHeadRow();
    }

    /**
     * @param columnHeadname
     * @param validator
     * @return this for chaining.
     */
    public ExcelSheet set(String columnHeadname, ColumnValidator validator) {
        ExcelColumnDef columnDef = getColumnDef(columnHeadname);
        if(columnDef == null) {
            log.error("Can't find column named '"  + columnHeadname + "'. Column validator ignored.");
            if (isMarkErrors()) {
                // TODO: Add message to excel head row (new column).
            }
            return this;
        }
        columnDef.setColumnValidator(validator);
        return this;
    }

    /**
     * @param columnNumber
     * @param validator
     * @return this for chaining.
     */
    public ExcelSheet set(int columnNumber, ColumnValidator validator) {
        ExcelColumnDef columnDef = getColumnDef(columnNumber);
        if(columnDef == null) {
            log.error("Can't get column number " + columnNumber + ". Column validator ignored.");
            if (isMarkErrors()) {
                // TODO: Add message to excel head row (new column).
            }
            return this;
        }
        columnDef.setColumnValidator(validator);
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
            String value = null;
            if (cell != null) {
                value = cell.getStringCellValue();
            }
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
        String value = null;
        if (cell != null) {
            value = cell.getStringCellValue();
        }
        ColumnValidator validator = columnDef.getColumnValidator();
        boolean required = validator != null ? validator.isRequired() : false;
        if (required && (value == null || value.length() == 0)) {
            log.error("Value of column '" + columnHeadname + "' required but not given in row #" + currentRow.getRowNum() + ".");
        }
        return value;
    }

    /**
     * @param columnDef
     * @return
     */
    public String getCell(ExcelColumnDef columnDef) {
        Cell cell = currentRow.getCell(columnDef.getColumnNumber());
        String value = null;
        if (cell != null) {
            value = cell.getStringCellValue();
        }
        ColumnValidator validator = columnDef.getColumnValidator();
        boolean required = validator != null ? validator.isRequired() : false;
        if (required && (value == null || value.length() == 0)) {
            log.error("Value of column '" + columnDef.getColumnHeadname() + "' required but not given in row #" + currentRow.getRowNum() + ".");
        }
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
            String val = cell.getStringCellValue();
            columnDefList.add(new ExcelColumnDef(col, StringUtils.defaultString(val)));
            if (val == null || val.length() == 0) {
                log.warn("Column head is empty for column " + col + " in 1st row.");
                continue;
            }
            log.debug("Reading head column '" + val + "' in column " + col);
            if (getColumnDef(val) != null) {
                log.warn("Duplicate column head: '" + val + "' in col #" + col);
            }
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

    /**
     * @param markErrors If true, any validation errors in the Excel file will be marked and validation messages will be added to the workbook and saved.
     */

    public void setMarkErrors(boolean markErrors) {
        this.markErrors = markErrors;
    }
}
