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
    private ExcelSheet sheet;

    public ExcelRow(ExcelSheet sheet, Row row) {
        this.sheet = sheet;
        this.row = row;
    }

    public int getRowNum() {
        return row.getRowNum();
    }

    public ExcelCell getCell(int columnNumber) {
        return getCell(columnNumber, null);
    }

    /**
     * @param columnDef Registered column definition.
     * @return The (created) cell. If column definition isn't known, an IllegalArgumentException will be thrown.
     */
    public ExcelCell getCell(ExcelColumnDef columnDef) {
        return getCell(columnDef, null);
    }

    /**
     * @param columnDef Registered column definition.
     * @param type      Only used, if new cell will be created.
     * @return The (created) cell. If column definition isn't known, an IllegalArgumentException will be thrown.
     */
    public ExcelCell getCell(ExcelColumnDef columnDef, ExcelCellType type) {
        return getCell(columnDef.getColumnNumber(), type);
    }

    /**
     * @param columnNumber The column number.
     * @param type         Only used, if new cell will be created.
     * @return The (created) cell, not null.
     */
    public ExcelCell getCell(int columnNumber, ExcelCellType type) {
        ExcelCell excelCell = cellMap.get(columnNumber);
        short lastCellNum = (short)(row.getLastCellNum() - 1);
        if (lastCellNum < 0) {
            lastCellNum = 0;
        }
        if (excelCell == null) {
            if (columnNumber <= lastCellNum) {
                return ensureCell(columnNumber, type);
            }
            for (short colNum = lastCellNum; colNum <= columnNumber; colNum++) {
                excelCell = ensureCell(colNum, type);
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
            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                cell = row.createCell(columnIndex, type != null ? type.getCellType() : CellType.STRING);
            }
            excelCell = ensureCell(cell);
        }
        return excelCell;
    }

    private ExcelCell ensureCell(Cell cell) {
        ExcelCell excelCell = cellMap.get(cell.getColumnIndex());
        if (excelCell == null) {
            excelCell = new ExcelCell(this, cell);
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

    public ExcelSheet getSheet() {
        return sheet;
    }

    public short getLastCellNum() {
        return row.getLastCellNum();
    }
}
