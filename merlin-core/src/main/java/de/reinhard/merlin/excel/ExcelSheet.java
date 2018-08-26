package de.reinhard.merlin.excel;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.ResultMessageStatus;
import de.reinhard.merlin.data.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExcelSheet {
    private Logger log = LoggerFactory.getLogger(ExcelSheet.class);
    public static final String MESSAGE_MISSING_COLUMN_NUMBER = "merlin.excel.validation_error.missing_column_number";
    public static final String MESSAGE_MISSING_COLUMN_BY_NAME = "merlin.excel.validation_error.missing_column_by_name";

    private List<ExcelColumnDef> columnDefList = new LinkedList<>();
    private Sheet poiSheet;
    private Iterator<Row> rowIterator;
    private Row currentRow;
    private final static int firstDataRow = 1; // 1st row (0) is head row.
    private Set<ExcelValidationErrorMessage> validationErrors;
    private boolean modified;

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
            createValidationErrorMissingColumnByName(columnHeadname);
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
            createValidationErrorMissingColumnNumber(columnNumber);
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
        listener.setSheet(this);
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
        return PoiHelper.getValueAsString(cell);
    }

    /**
     * @param columnDef
     * @return The cell of the specified column of the current row (uses internal interator).
     */
    public Cell getCell(ExcelColumnDef columnDef) {
        return currentRow.getCell(columnDef.getColumnNumber());
    }

    /**
     * @param row       Excel row number (starting with 0, POI row number).
     * @param columnDef
     * @return
     */
    public Cell getCell(int row, ExcelColumnDef columnDef) {
        return poiSheet.getRow(row).getCell(columnDef.getColumnNumber());
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

    public int getSheetIndex() {
        return poiSheet.getWorkbook().getSheetIndex(poiSheet);
    }

    public boolean hasValidationErrors() {
        if (validationErrors != null && validationErrors.size() > 0) {
            return true;
        }
        for (ExcelColumnDef columnDef : columnDefList) {
            if (!columnDef.hasColumnListeners()) {
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

    /**
     * @return All validation errors of this sheet including all validation errors of all registered {@link ColumnValidator}.
     * An empty set will be returned if no validation error was found.
     */
    public Set<ExcelValidationErrorMessage> getAllValidationErrors() {
        Set<ExcelValidationErrorMessage> allValidationErrors = new TreeSet<>();
        if (validationErrors != null) {
            allValidationErrors.addAll(validationErrors);
        }
        for (ExcelColumnDef columnDef : columnDefList) {
            if (!columnDef.hasColumnListeners()) {
                continue;
            }
            for (ColumnListener columnListener : columnDef.getColumnListeners()) {
                if (!(columnListener instanceof ColumnValidator)) {
                    continue;
                }
                ColumnValidator columnValidator = (ColumnValidator) columnListener;
                if (columnValidator.hasValidationErrors()) {
                    allValidationErrors.addAll(columnValidator.getValidationErrors());
                }
            }
        }
        return allValidationErrors;
    }

    public ExcelSheet markErrors() {
        return markErrors(new ExcelResponseContext(poiSheet.getWorkbook()));
    }

    public ExcelSheet markErrors(ResourceBundle resourceBundle) {
        return markErrors(resourceBundle, new ExcelResponseContext(poiSheet.getWorkbook()));
    }

    /**
     * Marks and comments validation errors of cells of this sheet by mamipulating the Excel sheet.
     * Refer {@link #isModified()} for checking if any modification was done.
     * Please don't forget to call {@link #analyze(boolean)} first with parameter validate=true.
     *
     * @param excelResponseContext Defines the type of response (how to display and highlight validation errors).
     * @return this for chaining.
     */
    public ExcelSheet markErrors(ExcelResponseContext excelResponseContext) {
        return markErrors(I18n.getInstance().getResourceBundle(), excelResponseContext);
    }

    /**
     * Marks and comments validation errors of cells of this sheet by mamipulating the Excel sheet.
     * Refer {@link #isModified()} for checking if any modification was done.
     * Please don't forget to call {@link #analyze(boolean)} first with parameter validate=true.
     *
     * @param resourceBundle       For localizing messages.
     * @param excelResponseContext Defines the type of response (how to display and highlight validation errors).
     * @return this for chaining.
     */
    public ExcelSheet markErrors(ResourceBundle resourceBundle, ExcelResponseContext excelResponseContext) {
        int validationErrorColumn = poiSheet.getRow(0).getLastCellNum();
        for (ExcelValidationErrorMessage validationError : getAllValidationErrors()) {
            ExcelColumnDef columnDef = validationError.getColumnDef();
            Row row = poiSheet.getRow(validationError.getRow());
            if (excelResponseContext.isAddErrorColumn()) {
                updateOrCreateCell(row, validationErrorColumn, validationError.getMessage(resourceBundle, ResultMessage.SUFFIX.KNOWN_ROW),
                        excelResponseContext.getErrorColumnCellStyle());
                modified = true;
            }
            if (columnDef != null) {
                Cell cell = row.getCell(columnDef.getColumnNumber());
                if (cell != null) {
                    if (excelResponseContext.isHighlightErrorCells()) {
                        // Cell validation error. Highlight cell.
                        cell.setCellStyle(excelResponseContext.getErrorHighlightCellStyle());
                        modified = true;
                    }
                    if (excelResponseContext.isAddCellComments()) {
                        // Cell validation error. Add error message as comment.
                        PoiHelper.setComment(cell, validationError.getMessage(resourceBundle, ResultMessage.SUFFIX.KNOWN_CELL));
                        modified = true;
                    }
                }
            }
        }
        if (modified) {
            // adjust column width to fit the content
            poiSheet.autoSizeColumn(validationErrorColumn);
        }
        return this;
    }

    private void updateOrCreateCell(Row row, int colNumber, String value, CellStyle cellStyle) {
        Cell cell = row.getCell(colNumber);
        if (cell == null) {
            cell = row.createCell(colNumber, CellType.STRING);
            cell.setCellStyle(cellStyle);
        }
        String actValue = cell.getStringCellValue();
        if (StringUtils.isBlank(actValue)) {
            cell.setCellValue(value);
        } else {
            CellStyle cs = poiSheet.getWorkbook().createCellStyle();
            cs.setWrapText(true);
            cell.setCellStyle(cs);
            //increase row height to accomodate one more line of text:
            float actHeight = row.getHeightInPoints();
            row.setHeightInPoints(actHeight + poiSheet.getDefaultRowHeightInPoints());
            cell.setCellValue(actValue + "\n" + value);
        }
    }

    /**
     * @return true, if this sheet was modified (by calling {@link #markErrors()}.
     */
    public boolean isModified() {
        return modified;
    }

    private void addValidationError(ExcelValidationErrorMessage message) {
        if (validationErrors == null) {
            validationErrors = new TreeSet<>();
        }
        validationErrors.add(message);
    }

    ExcelValidationErrorMessage createValidationErrorMissingColumnNumber(int columnNumber) {
        return new ExcelValidationErrorMessage(MESSAGE_MISSING_COLUMN_NUMBER, ResultMessageStatus.ERROR)
                .setSheet(this).setColumnDef(new ExcelColumnDef(columnNumber, ""));
    }

    ExcelValidationErrorMessage createValidationErrorMissingColumnByName(String columnName) {
        return new ExcelValidationErrorMessage(MESSAGE_MISSING_COLUMN_BY_NAME, ResultMessageStatus.ERROR)
                .setSheet(this).setColumnDef(new ExcelColumnDef(0, columnName));
    }
}
