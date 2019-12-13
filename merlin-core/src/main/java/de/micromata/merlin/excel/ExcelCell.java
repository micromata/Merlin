package de.micromata.merlin.excel;

import de.micromata.merlin.word.templating.TemplateRunContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import java.util.Date;

/**
 * Optional holder for POI cells. Useful for creating new cells.
 */
public class ExcelCell {
    private ExcelRow row;
    private Cell cell;
    private ExcelCellType type;

    ExcelCell(ExcelRow row, Cell cell) {
        this(row, cell, null, null);
    }

    ExcelCell(ExcelRow row, Cell cell, ExcelCellType type) {
        this(row, cell, type, null);
    }

    ExcelCell(ExcelRow row, Cell cell, ExcelCellType type, CellStyle cellStyle) {
        this.row = row;
        this.cell = cell;
        this.type = type;
        if (cellStyle != null) {
            this.cell.setCellStyle(cellStyle);
        }
    }

    public ExcelCell setCellValue(String str) {
        this.cell.setCellValue(str);
        return this;
    }

    /**
     * @param workbook Needed for creating int DataFormat.
     * @param intValue The value to set.
     * @return this for chaining.
     */
    public ExcelCell setCellValue(ExcelWorkbook workbook, int intValue) {
        setCellValue(workbook, cell, intValue);
        return this;
    }

    /**
     * @param workbook     Needed for creating int DataFormat.
     * @param booleanValue The value to set.
     * @return this for chaining.
     */
    public ExcelCell setCellValue(ExcelWorkbook workbook, boolean booleanValue) {
        setCellValue(workbook, cell, booleanValue);
        return this;
    }

    /**
     * @param workbook    Needed for creating int DataFormat.
     * @param doubleValue The value to set.
     * @return this for chaining.
     */
    public ExcelCell setCellValue(ExcelWorkbook workbook, double doubleValue) {
        setCellValue(workbook, cell, doubleValue);
        return this;
    }

    public ExcelCell setCellStyle(CellStyle style) {
        cell.setCellStyle(style);
        return this;
    }

    public Cell getCell() {
        return cell;
    }

    public String getStringCellValue() {
        return cell.getStringCellValue();
    }

    public void evaluateFormularCell() {
        row.getSheet().getExcelWorkbook().getFormelEvaluator().evaluateFormulaCell(cell);
    }

    public static void setCellValue(ExcelWorkbook workbook, Cell cell, double doubleValue) {
        cell.setCellValue(doubleValue);
        cell.setCellStyle(workbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT));
    }

    public static void setCellValue(ExcelWorkbook workbook, Cell cell, int intValue) {
        cell.setCellValue((double) intValue);
        cell.setCellStyle(workbook.ensureCellStyle(ExcelCellStandardFormat.INT));
    }

    public static void setCellValue(ExcelWorkbook workbook, Cell cell, boolean booleanValue) {
        cell.setCellValue(TemplateRunContext.getBooleanAsString(booleanValue));
        cell.setCellStyle(workbook.ensureCellStyle(ExcelCellStandardFormat.INT));
    }

    public static void setCellValue(ExcelWorkbook workbook, Cell cell, String format, Date dateValue) {
        cell.setCellValue(dateValue);
        cell.setCellStyle(workbook.ensureDateCellStyle(format));
    }
}
