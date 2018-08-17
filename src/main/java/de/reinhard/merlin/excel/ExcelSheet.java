package de.reinhard.merlin.excel;

import de.reinhard.merlin.data.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.util.*;

public class ExcelSheet {
    private static final Logger log = Logger.getLogger(ExcelSheet.class);

    private Map<String, Integer> colMap = new HashMap<>();
    private List<String> headCells = new LinkedList<>();
    private Sheet poiSheet;
    private Iterator<Row> rowIterator;
    private Row currentRow;
    private List<ColumnValidator> colValidators = new LinkedList<>();
    boolean markErrors;

    public ExcelSheet(Sheet poiSheet) {
        log.info("Reading sheet '" + poiSheet.getSheetName() + "'");
        this.poiSheet = poiSheet;
        rowIterator = poiSheet.iterator();
        readHeadRow();
    }

    /**
     * @param validator
     * @return this for chaining.
     */
    public ExcelSheet add(ColumnValidator validator) {
        if (getColValidator(validator.getColumnHeadname()) != null) {
            log.error("Oups, trying to add column validator '" + validator.getColumnHeadname() + "' twice. Ignoring duplicate validator.");
            return this;
        }
        colValidators.add(validator);
        return this;
    }

    public boolean hasNextRow() {
        return rowIterator.hasNext();
    }

    public void nextRow() {
        currentRow = rowIterator.next();
    }

    public void readRow(Data data) {
        for (String head : headCells) {
            String val = getCell(head);
            data.put(head, val);
        }
    }

    /**
     * @param colhead
     * @return
     */
    public String getCell(String colhead) {
        Integer col = colMap.get(colhead);
        if (col == null) {
            col = colMap.get(colhead.toLowerCase());
        }
        if (col == null) {
            log.warn("No entry named '" + colhead + "' found in sheet '" + currentRow.getSheet().getSheetName() + "'. Checked also '" + colhead.toLowerCase() + "'.");
            return null;
        }
        Cell cell = currentRow.getCell(col);
        String value = null;
        if (cell != null) {
            value = cell.getStringCellValue();
        }
        ColumnValidator validator = getColValidator(colhead);
        boolean required = validator != null ? validator.isRequired() : false;
        if (required && (value == null || value.length() == 0)) {
            log.error("Value of column '" + colhead + "' required but not given in row #" + currentRow.getRowNum() + ".");
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
            headCells.add(StringUtils.defaultString(val));
            if (val == null || val.length() == 0) {
                log.warn("Column head is empty for column " + col + " in 1st row.");
                continue;
            }
            log.debug("Reading head column '" + val + "' in column " + col);
            if (colMap.containsKey(val)) {
                log.warn("Duplicate column head: '" + val + "' in col #" + col);
                continue; // Don't overwrite.
            }
            colMap.put(val, col);
            String lowerVal = val.toLowerCase();
            if (val.equals(lowerVal)) {
                continue;
            }
            if (colMap.containsKey(lowerVal)) {
                log.warn("Duplicate column head: '" + lowerVal + "' in col #" + col);
                continue; // Don't overwrite.
            }
            colMap.put(val.toLowerCase(), col); // Store value also as to Lower;
        }
    }

    public ColumnValidator getColValidator(String colHead) {
        if (StringUtils.isEmpty(colHead)) {
            return null;
        }
        for (ColumnValidator validator : colValidators) {
            if (colHead.equals(validator.getColumnHeadname())) {
                return validator;
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
