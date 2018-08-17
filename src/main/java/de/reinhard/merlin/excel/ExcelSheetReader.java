package de.reinhard.merlin.excel;

import de.reinhard.merlin.data.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.util.*;

public class ExcelSheetReader {
    private static final Logger log = Logger.getLogger(ExcelSheetReader.class);

    private Map<String, Integer> colMap = new HashMap<>();

    private List<String> headCells = new LinkedList<>();

    private Sheet datatypeSheet;

    private Iterator<Row> rowIterator;

    private Row currentRow;

    public ExcelSheetReader(Sheet datatypeSheet) {
        log.info("Reading sheet '" + datatypeSheet.getSheetName() + "'");
        this.datatypeSheet = datatypeSheet;
        rowIterator = datatypeSheet.iterator();
        readHeadRow();
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

    public String getCell(String colhead) {
        return getCell(colhead, false);
    }

    /**
     * @param colhead
     * @param required if true, then an error message will be logged, if value not given.
     * @return
     */
    public String getCell(String colhead, boolean required) {
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
        if (required && (value == null || value.length() == 0)) {
            log.error("Value of column '" + colhead + "' required but not given in row #" + currentRow.getRowNum() + ".");
        }
        return value;
    }

    private void readHeadRow() {
        if (!rowIterator.hasNext()) {
            log.info("Sheet '" + datatypeSheet.getSheetName() + "' has now rows.");
            return;
        }
        log.info("Reading head row of sheet '" + datatypeSheet.getSheetName() + "'.");
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
}
