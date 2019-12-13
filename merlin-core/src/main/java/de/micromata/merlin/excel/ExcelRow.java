package de.micromata.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper wrapper for creating rows in a more convenient way.
 */
public class ExcelRow {
    private Row row;
    private Map<Integer, ExcelCell> cellMap = new HashMap<>();

    public ExcelRow(Row row) {
        this.row = row;
    }

    public int getRowNum() {
        return row.getRowNum();
    }

    public ExcelCell getCell(int columnNumber) {
        return getCell(columnNumber, null);
    }

    /**
     * @param columnDef
     * @return not null
     */
    public ExcelCell getCell(ExcelColumnDef columnDef) {
        return getCell(columnDef, null);
    }

    /**
     * @param columnDef
     * @param type      Only used, if new cell will be created.
     * @return not null
     */
    public ExcelCell getCell(ExcelColumnDef columnDef, ExcelCellType type) {
        return getCell(columnDef.getColumnNumber(), type);
    }

    /**
     * @param columnNumber
     * @param type         Only used, if new cell will be created.
     * @return not null
     */
    public ExcelCell getCell(int columnNumber, ExcelCellType type) {
        ExcelCell excelCell = cellMap.get(columnNumber);
        if (excelCell == null) {
            Cell cell;
            while ((cell = row.getCell(columnNumber)) == null) {
                excelCell = createCell(type);
            }
            if (excelCell == null) { // Poi cell exists, but excel cell not yet:
                excelCell = ensureCell(columnNumber, type);
            }
        }
        return excelCell;
    }

    /**
     * Assumes {@link ExcelCellType#STRING}
     *
     * @return The created cell.
     */
    public ExcelCell createCell() {
        return createCell(ExcelCellType.STRING);
    }

    public ExcelCell createCell(ExcelCellType type) {
        int colCount = row.getLastCellNum();
        if (colCount < 0) {
            colCount = 0;
        }
        ExcelCell excelCell = ensureCell(colCount, type);
        return excelCell;
    }

    private ExcelCell ensureCell(int columnIndex, ExcelCellType type) {
        ExcelCell excelCell = cellMap.get(columnIndex);
        if (excelCell == null) {
            Cell cell = row.createCell(columnIndex, type != null ? type.getCellType() : CellType.STRING);
            excelCell = ensureCell(cell);
        }
        return excelCell;
    }

    private ExcelCell ensureCell(Cell cell) {
        ExcelCell excelCell = cellMap.get(cell.getColumnIndex());
        if (excelCell == null) {
            excelCell = new ExcelCell(cell);
            cellMap.put(cell.getColumnIndex(), excelCell);
        }
        return excelCell;
    }

    public void createCells(String... cells) {
        createCells(null, cells);
    }

    public void createCells(CellStyle cellStyle, String... cells) {
        for (String cellString : cells) {
            ExcelCell cell = this.createCell(ExcelCellType.STRING);
            cell.setCellValue(cellString);
            if (cellStyle != null) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    public ExcelRow setHeight(float height) {
        row.setHeightInPoints(height);
        return this;
    }

    public void addMergeRegion(int fromCol, int toCol) {
        CellRangeAddress range = new CellRangeAddress(row.getRowNum(), row.getRowNum(), fromCol, toCol);
        row.getSheet().addMergedRegion(range);
    }

    public float getHeightInPoints() {
        return row.getHeightInPoints();
    }

    public void setHeightInPoints(float height) {
        row.setHeightInPoints(height);
    }

    public Row getRow() {
        return row;
    }
}
