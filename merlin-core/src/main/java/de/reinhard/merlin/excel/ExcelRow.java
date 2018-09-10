package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelRow {
    private Row row;

    ExcelRow(Row row) {
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
        for (String cellString : cells) {
            ExcelCell cell = this.createCell(ExcelCellType.STRING);
            cell.setCellValue(cellString);
        }
    }
}
