package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * A column listener assigned to a {@link ExcelColumnDef} listens to all read cell values.
 */
public abstract class ColumnListener {
    protected ExcelColumnDef columnDef;
    protected ExcelSheet sheet;

    abstract void readCell(Cell cell, int rowNumber);

    void setColumnDef(ExcelColumnDef columnDef) {
        this.columnDef = columnDef;
    }

    public ExcelSheet getSheet() {
        return sheet;
    }

    void setSheet(ExcelSheet sheet) {
        if (this.sheet != null && sheet != this.sheet) {
            throw new IllegalArgumentException("Cant't add Columnlistener to different sheets. Please don't re-use same instance of ColumnListener.");
        }
        this.sheet = sheet;
    }
}
