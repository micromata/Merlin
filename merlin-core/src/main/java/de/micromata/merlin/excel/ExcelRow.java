package de.micromata.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * A helper wrapper for creating rows in a more convenient way.
 */
public class ExcelRow {
    private Row row;

    public ExcelRow(Row row) {
        this.row = row;
    }

    /**
     * Assumes {@link ExcelCellType#STRING}
     *
     * @return
     */
    public ExcelCell createCell() {
        return createCell(ExcelCellType.STRING);
    }

    public ExcelCell createCell(ExcelCellType type) {
        int colCount = row.getLastCellNum();
        if (colCount < 0) {
            colCount = 0;
        }
        Cell cell = row.createCell(colCount, type.getCellType());
        return new ExcelCell(cell, type);
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
}
