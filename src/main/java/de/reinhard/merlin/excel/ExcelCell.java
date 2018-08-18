package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelCell {
    private ExcelColumnDef columnDef;
    private Cell poiCell;

    ExcelCell(ExcelColumnDef columnDef, Cell poiCell) {
        this.columnDef = columnDef;
        this.poiCell = poiCell;
    }

    public ExcelColumnDef getColumnDef() {
        return columnDef;
    }

    public String getStringCellValue() {
        return poiCell.getStringCellValue();
    }
}
