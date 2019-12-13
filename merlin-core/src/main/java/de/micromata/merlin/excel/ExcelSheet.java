package de.micromata.merlin.excel;

import de.micromata.merlin.CoreI18n;
import de.micromata.merlin.I18n;
import de.micromata.merlin.ResultMessageStatus;
import de.micromata.merlin.data.Data;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * Wraps and enhances a POI sheet.
 */
public class ExcelSheet {
    private Logger log = LoggerFactory.getLogger(ExcelSheet.class);
    public static final String MESSAGE_MISSING_COLUMN_NUMBER = "merlin.excel.validation_error.missing_column_number";
    public static final String MESSAGE_MISSING_COLUMN_BY_NAME = "merlin.excel.validation_error.missing_column_by_name";

    private List<ExcelColumnDef> columnDefList = new ArrayList<>();
    private Sheet poiSheet;
    private ExcelWorkbook workbook;
    private ExcelRow headRow = null;
    private int columnWithValidationErrorMessages = -1;
    private Set<ExcelValidationErrorMessage> validationErrors;
    private boolean modified;
    private I18n i18n;
    private int maxMarkedErrors = 100;
    private Map<Integer, ExcelRow> excelRowMap = new HashMap<>();

    ExcelSheet(ExcelWorkbook workbook, Sheet poiSheet) {
        log.debug("Reading sheet '" + poiSheet.getSheetName() + "'");
        this.workbook = workbook;
        this.poiSheet = poiSheet;
        this.i18n = CoreI18n.getDefault();
    }

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    /**
     * Forces reloading of head row. This is useful if you get the head row, read it manually to add new
     * {@link ExcelColumnDef} objects and to restart.
     */
    public void reset() {
        headRow = null;
        modified = false;
        validationErrors = null;
    }

    /**
     * Analyzes sheet.
     * <p>
     * Each cell will be analyzed by calling ExcelColumnListener for each column with given
     * ExcelColumnListener. If no Analyzer is set for a columng, the column cells will not be analyzed.
     *
     * @param validate if true, then each cell of a column with a given ExcelColumnValidator will be validated.
     * @return this for chaining.
     */
    public ExcelSheet analyze(boolean validate) {
        findAndReadHeadRow();
        if (validate) {
            // Detect missing columns:
            for (ExcelColumnDef columnDef : columnDefList) {
                if (!columnDef.hasColumnListeners()) {
                    continue;
                }
                for (ExcelColumnListener listener : columnDef.getColumnListeners()) {
                    if (listener instanceof ExcelColumnValidator) {
                        if (columnDef.getColumnNumber() < 0) {
                            addValidationError(createValidationErrorMissingColumnByName(columnDef.getColumnHeadname()));
                        }
                    }
                }
            }
        }
        Iterator<Row> it = getDataRowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            if (row.getLastCellNum() > columnWithValidationErrorMessages) {
                columnWithValidationErrorMessages = row.getLastCellNum();
            }
            for (ExcelColumnDef columnDef : columnDefList) {
                if (!columnDef.hasColumnListeners() || columnDef.getColumnNumber() < 0) {
                    continue;
                }
                for (ExcelColumnListener listener : columnDef.getColumnListeners()) {
                    if (!(listener instanceof ExcelColumnValidator) || validate) {
                        Cell cell = row.getCell(columnDef.getColumnNumber());
                        listener.readCell(cell, row.getRowNum());
                    }
                }
            }
        }
        return this;
    }

    /**
     * @param columnHeads The column heads to register.
     * @return this for chaining.
     */
    public ExcelSheet registerColumns(String... columnHeads) {
        for (String columnHead : columnHeads) {
            if (_getColumnDef(columnHead) != null) {
                log.error("Don't register column heads twice: '" + columnHead + "'.");
                continue;
            }
            columnDefList.add(new ExcelColumnDef(columnHead));
        }
        return this;
    }

    /**
     * @param columnHead The column head to register.
     * @return Created and registered ExcelColumnDef.
     */
    public ExcelColumnDef registerColumn(String columnHead) {
        if (_getColumnDef(columnHead) != null) {
            log.error("Don't register column heads twice: '" + columnHead + "'.");
            return _getColumnDef(columnHead);
        }
        ExcelColumnDef columnDef = new ExcelColumnDef(columnHead);
        columnDefList.add(columnDef);
        return columnDef;
    }

    /**
     * @param columnHead The column head to register.
     * @param listener   The listener to use.
     * @return Created and registered ExcelColumnDef.
     */
    public ExcelColumnDef registerColumn(String columnHead, ExcelColumnListener listener) {
        ExcelColumnDef columnDef = _getColumnDef(columnHead);
        if (columnDef == null) {
            columnDef = new ExcelColumnDef(columnHead);
            columnDefList.add(columnDef);
        }
        registerColumn(columnDef, listener);
        return columnDef;
    }

    /**
     * @param columnDef The column to register.
     * @param listener  The listener to use.
     * @return this for chaining.
     */
    public ExcelSheet registerColumn(ExcelColumnDef columnDef, ExcelColumnListener listener) {
        columnDef.addColumnListener(listener);
        listener.setSheet(this);
        return this;
    }

    /**
     * @return Iterator for rows. Iterator starts with data row (head row + 1).
     */
    public Iterator<Row> getDataRowIterator() {
        findAndReadHeadRow();
        Iterator<Row> it = poiSheet.rowIterator();
        while (it.hasNext()) {
            if (it.next().equals(headRow.getRow())) {
                break;
            }
        }
        return it;
    }

    public void readRow(Row row, Data data) {
        findAndReadHeadRow();
        for (ExcelColumnDef columnDef : columnDefList) {
            Cell cell = row.getCell(columnDef.getColumnNumber());
            String value = PoiHelper.getValueAsString(cell);
            data.put(columnDef.getColumnHeadname(), value);
        }
    }

    /**
     * @param row            The row to get the cell value from.
     * @param columnHeadname The name of the column to get.
     * @return The String value of the specified column cell.
     */
    public String getCellString(Row row, String columnHeadname) {
        // findAndReadHeadRow(); Will be called in getColumnDef
        ExcelColumnDef columnDef = getColumnDef(columnHeadname);
        if (columnDef == null) {
            log.warn("No entry named '" + columnHeadname + "' found in sheet '" + row.getSheet().getSheetName()
                    + "'. Checked also '" + columnHeadname.toLowerCase() + "'.");
            return null;
        }
        int colnumber = columnDef.getColumnNumber();
        if (colnumber == -1) {
            log.warn("Column " + columnDef.getColumnNumberAsLetters() + " named '" + columnDef.getColumnHeadname() + "' not found.");
            return null;
        }
        Cell cell = row.getCell(colnumber);
        return PoiHelper.getValueAsString(cell);
    }

    /**
     * @param row            The row to get the cell from.
     * @param columnHeadname The name of the column to get the cell from.
     * @return The cell of the specified column of the current row (uses internal interator).
     */
    public Cell getCell(Row row, String columnHeadname) {
        // findAndReadHeadRow(); Will be called in getColumnDef
        ExcelColumnDef columnDef = getColumnDef(columnHeadname);
        return getCell(row, columnDef);
    }

    /**
     * @param row       The row to get the cell from.
     * @param columnDef The specified column to get the cell from.
     * @return The cell of the specified column of the current row (uses internal interator).
     */
    public Cell getCell(Row row, ExcelColumnDef columnDef) {
        findAndReadHeadRow();
        if (columnDef == null) {
            return null;
        }
        if (columnDef.getColumnNumber() < 0) {
            log.debug("Column '" + columnDef.getColumnHeadname() + "' not found in sheet '" + getSheetName() + "': can't run cell.");
            return null;
        }
        return row.getCell(columnDef.getColumnNumber());
    }

    /**
     * @param row       Excel row number (starting with 0, POI row number).
     * @param columnDef The specified column to get the cell from.
     * @return The specified cell.
     */
    public Cell getCell(int row, ExcelColumnDef columnDef) {
        findAndReadHeadRow();
        return getCell(row, columnDef.getColumnNumber());
    }

    /**
     * @param rowNum       Excel row number (starting with 0, POI row number).
     * @param columnNumber The specified column to get the cell from.
     * @return The specified cell.
     */
    public Cell getCell(int rowNum, int columnNumber) {
        findAndReadHeadRow();
        return getRow(rowNum).getCell(columnNumber).getCell();
    }

    private void findAndReadHeadRow() {
        if (headRow != null) {
            return; // head row already run.
        }
        log.debug("Reading head row of sheet '" + poiSheet.getSheetName() + "'.");
        Iterator<Row> rowIterator = poiSheet.rowIterator();
        Row current = null;
        for (int i = 0; i < 10; i++) { // Detect head row, check Row 0-9 for column heads.
            if (!rowIterator.hasNext()) {
                break;
            }
            log.debug("Parsing row #" + i + " of sheet '" + poiSheet.getSheetName() + "'.");
            current = rowIterator.next();
            if (current.getLastCellNum() > columnWithValidationErrorMessages) {
                columnWithValidationErrorMessages = current.getLastCellNum();
            }
            int col = -1;
            for (Cell cell : current) {
                ++col;
                String val = PoiHelper.getValueAsString(cell);
                log.debug("Reading cell '" + val + "' in column " + col);
                if (_getColumnDef(val) != null) {
                    log.debug("Head column found: '" + val + "' in col #" + col);
                    headRow = ensureRow(current);
                    break;
                }
            }
            if (headRow != null) {
                break;
            }
        }
        if (headRow == null || current == null) {
            log.debug("No head row found in sheet '" + getSheetName() + "'.");
            return;
        }
        // Now run all columns for assigning column numbers to column definitions.
        int col = -1;
        for (Cell cell : current) {
            ++col;
            String val = PoiHelper.getValueAsString(cell);
            log.debug("Reading head column '" + val + "' in column " + col);
            ExcelColumnDef columnDef = _getColumnDef(val);
            if (columnDef != null) {
                log.debug("Head column found: '" + val + "' in col #" + col);
                columnDef.setColumnNumber(col);
            } else {
                log.debug("Head column not registered: '" + val + "'.");
            }
        }
    }

    public ExcelColumnDef getColumnDef(String columnHeadname) {
        findAndReadHeadRow();
        return _getColumnDef(columnHeadname);
    }

    public ExcelColumnDef _getColumnDef(String columnHeadname) {
        if (StringUtils.isEmpty(columnHeadname)) {
            return null;
        }
        String lowerColumnHeadname = columnHeadname.toLowerCase().trim();
        for (ExcelColumnDef columnDef : columnDefList) {
            if (lowerColumnHeadname.equals(columnDef.getColumnHeadname().toLowerCase().trim())) {
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

    /**
     * Don't forget to call analyze(true) first.
     * Is an alias for !{@link #hasValidationErrors()}.
     *
     * @return true if no error messages exist, otherwise false.
     */
    public boolean isValid() {
        return !hasValidationErrors();
    }

    /**
     * Don't forget to call analyze(true) first.
     *
     * @return true if no error messages exist, otherwise false.
     */
    public boolean hasValidationErrors() {
        if (validationErrors != null && validationErrors.size() > 0) {
            return true;
        }
        for (ExcelColumnDef columnDef : columnDefList) {
            if (!columnDef.hasColumnListeners()) {
                continue;
            }
            for (ExcelColumnListener columnListener : columnDef.getColumnListeners()) {
                if (!(columnListener instanceof ExcelColumnValidator)) {
                    continue;
                }
                if (((ExcelColumnValidator) columnListener).hasValidationErrors()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return All validation errors of this sheet including all validation errors of all registered {@link ExcelColumnValidator}.
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
            for (ExcelColumnListener columnListener : columnDef.getColumnListeners()) {
                if (!(columnListener instanceof ExcelColumnValidator)) {
                    continue;
                }
                ExcelColumnValidator columnValidator = (ExcelColumnValidator) columnListener;
                if (columnValidator.hasValidationErrors()) {
                    allValidationErrors.addAll(columnValidator.getValidationErrors());
                }
            }
        }
        return allValidationErrors;
    }

    public ExcelSheet markErrors() {
        return markErrors(new ExcelWriterContext(i18n, workbook));
    }

    public ExcelSheet markErrors(I18n i18n) {
        return markErrors(i18n, new ExcelWriterContext(i18n, workbook));
    }

    /**
     * Marks and comments validation errors of cells of this sheet by mamipulating the Excel sheet.
     * Refer {@link #isModified()} for checking if any modification was done.
     * Please don't forget to call {@link #analyze(boolean)} first with parameter validate=true.
     *
     * @param excelWriterContext Defines the type of response (how to display and highlight validation errors).
     * @return this for chaining.
     */
    public ExcelSheet markErrors(ExcelWriterContext excelWriterContext) {
        return markErrors(i18n, excelWriterContext);
    }

    /**
     * Marks and comments validation errors of cells of this sheet by manipulating the Excel sheet.
     * Refer {@link #isModified()} for checking if any modification was done.
     * Please don't forget to call {@link #analyze(boolean)} first with parameter validate=true.
     *
     * @param i18N               For localizing messages.
     * @param excelWriterContext Defines the type of response (how to display and highlight validation errors).
     * @return this for chaining.
     */
    public ExcelSheet markErrors(I18n i18N, ExcelWriterContext excelWriterContext) {
        columnWithValidationErrorMessages = excelWriterContext.getCellCleaner().clean(this, excelWriterContext);
        analyze(true);
        Set<ExcelColumnDef> highlightedColumnHeads = new HashSet<>();
        int errorCount = 0;
        for (ExcelValidationErrorMessage validationError : getAllValidationErrors()) {
            if (maxMarkedErrors >= 0 && ++errorCount > maxMarkedErrors) {
                // Maximum number of errors to mark is exceeded.
                break;
            }
            ExcelColumnDef columnDef = validationError.getColumnDef();
            ExcelRow row = getRow(validationError.getRow());
            if (excelWriterContext.isAddErrorColumn()) {
                excelWriterContext.getErrorMessageWriter().updateOrCreateCell(excelWriterContext, this,
                        columnWithValidationErrorMessages, row, validationError);
                modified = true;
            }
            if (columnDef != null) {
                ExcelCell cell = row.getCell(columnDef.getColumnNumber());
                if (cell == null) {
                    cell = row.getCell(columnDef.getColumnNumber(), ExcelCellType.STRING);
                }
                if (excelWriterContext.isHighlightErrorCells()) {
                    // Cell validation error. Highlight cell.
                    excelWriterContext.getCellHighlighter().highlightErrorCell(cell, excelWriterContext, this,
                            columnDef, row);
                    modified = true;
                }
                if (excelWriterContext.isHighlightColumnHeadCells()) {
                    if (headRow != null && !highlightedColumnHeads.contains(columnDef)) {
                        highlightedColumnHeads.add(columnDef); // Don't highlight column heads twice.
                        // Cell validation error. Highlight column head cell.
                        ExcelCell headCell = headRow.getCell(columnDef.getColumnNumber());
                        excelWriterContext.getCellHighlighter().highlightColumnHeadCell(headCell, excelWriterContext, this,
                                columnDef, headRow);
                        modified = true;
                    }
                }
                if (excelWriterContext.isAddCellComments()) {
                    // Cell validation error. Add error message as comment.
                    excelWriterContext.getCellHighlighter().setCellComment(cell, excelWriterContext, this,
                            columnDef, row, validationError.getMessage(i18n));
                    modified = true;
                }
            }
        }
        if (modified) {
            // adjust column width to fit the content
            poiSheet.autoSizeColumn(columnWithValidationErrorMessages);
        }
        return this;
    }

    public Cell setBigDecimalValue(int row, String columnHeader, BigDecimal value) {
        return setBigDecimalValue(row, getColumnDef(columnHeader), value);
    }

    public Cell setBigDecimalValue(int row, ExcelColumnDef col, BigDecimal value) {
        return setBigDecimalValue(row, col.getColumnNumber(), value);
    }

    public Cell setBigDecimalValue(int row, int col, BigDecimal value) {
        Cell cell = getCell(row, col);
        if (value == null) cell.setBlank();
        else {
            cell.setCellValue(value.doubleValue());
            cell.setCellStyle(workbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT));
        }
        return cell;
    }

    public Cell setDoubleValue(int row, String columnHeader, Double value) {
        return setDoubleValue(row, getColumnDef(columnHeader), value);
    }

    public Cell setDoubleValue(int row, ExcelColumnDef col, Double value) {
        return setDoubleValue(row, col.getColumnNumber(), value);
    }

    public Cell setDoubleValue(int row, int col, Double value) {
        Cell cell = getCell(row, col);
        if (value == null) cell.setBlank();
        else {
            cell.setCellValue(value);
            cell.setCellStyle(workbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT));
        }
        return cell;
    }

    public Cell setIntValue(int row, String columnHeader, Integer value) {
        return setIntValue(row, getColumnDef(columnHeader), value);
    }

    public Cell setIntValue(int row, ExcelColumnDef col, Integer value) {
        return setIntValue(row, col.getColumnNumber(), value);
    }

    public Cell setIntValue(int row, int col, Integer value) {
        Cell cell = getCell(row, col);
        if (value == null) cell.setBlank();
        else {
            cell.setCellValue(value);
            cell.setCellStyle(workbook.ensureCellStyle(ExcelCellStandardFormat.INT));
        }
        return cell;
    }

    public Cell setStringValue(int row, String columnHeader, String value) {
        return setStringValue(row, getColumnDef(columnHeader), value);
    }

    public Cell setStringValue(int row, ExcelColumnDef col, String value) {
        return setStringValue(row, col.getColumnNumber(), value);
    }

    public Cell setStringValue(int row, int col, String value) {
        Cell cell = getCell(row, col);
        if (value == null) cell.setBlank();
        else cell.setCellValue(value);
        return cell;
    }

    public Cell setDateValue(int row, String columnHeader, Date value, String dateFormat) {
        return setDateValue(row, getColumnDef(columnHeader), value, dateFormat);
    }

    public Cell setDateValue(int row, ExcelColumnDef col, Date value, String dateFormat) {
        return setDateValue(row, col.getColumnNumber(), value, dateFormat);
    }

    public Cell setDateValue(int row, int col, Date value, String dateFormat) {
        val cell = getCell(row, col);
        if (value == null) cell.setBlank();
        else {
            cell.setCellValue(value);
            cell.setCellStyle(workbook.ensureDateCellStyle(dateFormat));
        }
        return cell;
    }

    /**
     * @param maxMarkedErrors The number of marked errors should be limited (default is 100). Otherwise for very large Excel sheet with a lot of errors, the
     *                        system may be collapse.
     *                        <br>
     *                        If set to -1 an unlimited number of errors will be marked.
     * @return This for chaining.
     */
    public ExcelSheet setMaxMarkedErrors(int maxMarkedErrors) {
        this.maxMarkedErrors = maxMarkedErrors;
        return this;
    }

    /**
     * @return true, if this sheet was modified (by calling {@link #markErrors()}.
     */
    public boolean isModified() {
        return modified;
    }

    void setModified(boolean modified) {
        this.modified = modified;
    }

    private void addValidationError(ExcelValidationErrorMessage message) {
        if (validationErrors == null) {
            validationErrors = new TreeSet<>();
        }
        validationErrors.add(message);
    }

    ExcelValidationErrorMessage createValidationErrorMissingColumnNumber(int columnNumber) {
        return new ExcelValidationErrorMessage(MESSAGE_MISSING_COLUMN_NUMBER, ResultMessageStatus.ERROR,
                CellReference.convertNumToColString(columnNumber))
                .setSheet(this).setRow(headRow != null ? headRow.getRowNum() : 0);
    }

    ExcelValidationErrorMessage createValidationErrorMissingColumnByName(String columnName) {
        return new ExcelValidationErrorMessage(MESSAGE_MISSING_COLUMN_BY_NAME, ResultMessageStatus.ERROR, columnName)
                .setSheet(this).setRow(headRow != null ? headRow.getRowNum() : 0);
    }

    public Sheet getPoiSheet() {
        return poiSheet;
    }

    public ExcelWorkbook getExcelWorkbook() {
        return workbook;
    }

    public ExcelRow getRow(int rownum) {
        ExcelRow excelRow = excelRowMap.get(rownum);
        if (excelRow == null) {
            Row row;
            while ((row = poiSheet.getRow(rownum)) == null) {
                excelRow = createRow();
            }
        }
        return excelRow;
    }

    public ExcelRow getHeadRow() {
        findAndReadHeadRow();
        return headRow;
    }

    public void cleanSheet() {
        int numberOfRows = poiSheet.getLastRowNum();
        if (numberOfRows >= 0) {
            return; // Nothing to do.
        }
        modified = true;
        for (int i = numberOfRows; i >= 0; i--) {
            if (poiSheet.getRow(i) != null) {
                poiSheet.removeRow(poiSheet.getRow(i));
            }
        }
    }

    /**
     * Appends the row.
     *
     * @return The created row.
     */
    public ExcelRow createRow() {
        int rowCount = poiSheet.getLastRowNum();
        if (rowCount == 0 && poiSheet.getRow(0) == null) {
            rowCount = -1;
        }
        Row row = poiSheet.createRow(rowCount + 1);
        return ensureRow(row);
    }

    private ExcelRow ensureRow(Row row) {
        ExcelRow excelRow = excelRowMap.get(row.getRowNum());
        if (excelRow == null) {
            excelRow = new ExcelRow(row);
            excelRowMap.put(row.getRowNum(), excelRow);
        }
        return excelRow;
    }

    public void autosize() {
        for (int i = 0; i <= getLastColumn(); i++) {
            poiSheet.autoSizeColumn(i);
        }
    }

    public void autosize(int columnIndex) {
        poiSheet.autoSizeColumn(columnIndex);
    }

    public void setColumnWidth(int columnIndex, int width) {
        poiSheet.setColumnWidth(columnIndex, width);
    }

    public void addMergeRegion(CellRangeAddress range) {
        poiSheet.addMergedRegion(range);
    }

    public int getLastColumn() {
        int lastCol = 0;
        for (Row row : poiSheet) {
            if (row.getLastCellNum() > lastCol) {
                lastCol = row.getLastCellNum();
            }
        }
        return lastCol;
    }
}
