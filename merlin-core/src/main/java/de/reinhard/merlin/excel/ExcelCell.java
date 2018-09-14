package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;

import java.util.Date;

public class ExcelCell {
    private Cell cell;
    private ExcelCellType type;

    ExcelCell(Cell cell, ExcelCellType type) {
        this(cell, type, null);
    }

    ExcelCell(Cell cell, ExcelCellType type, CellStyle cellStyle) {
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
     * @param intValue
     * @return this for chaining.
     */
    public ExcelCell setCellValue(ExcelWorkbook workbook, int intValue) {
        setCellValue(workbook, cell, intValue);
        return this;
    }

    /**
     * @param workbook    Needed for creating int DataFormat.
     * @param doubleValue
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

    public static void setCellValue(ExcelWorkbook workbook, Cell cell, double doubleValue) {
        cell.setCellValue(doubleValue);
        CellStyle cellStyle = workbook.createOrGetCellStyle("DataFormat.float");
        cellStyle.setDataFormat(workbook.getPOIWorkbook().getCreationHelper().createDataFormat().getFormat("#.#"));
        cell.setCellStyle(cellStyle);
    }

    public static void setCellValue(ExcelWorkbook workbook, Cell cell, int intValue) {
        cell.setCellValue((double) intValue);
        CellStyle cellStyle = workbook.createOrGetCellStyle("DataFormat.int");
        cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0"));
        cell.setCellStyle(cellStyle);
    }

    public static void setCellValue(ExcelWorkbook workbook, Cell cell, String format, Date dateValue) {
        CellStyle cellStyle = workbook.createOrGetCellStyle("DataFormat.date");
        CreationHelper createHelper = workbook.getPOIWorkbook().getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(format));
        cell.setCellValue(dateValue);
        cell.setCellStyle(cellStyle);
    }
}
