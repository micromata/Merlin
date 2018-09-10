package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

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

    public void setCellValue(String str) {
        this.cell.setCellValue(str);
    }

    public void setCellStyle(CellStyle style) {
        cell.setCellStyle(style);
    }
}
