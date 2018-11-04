package de.micromata.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * A column listener assigned to a {@link ExcelColumnDef} listens to all read cell values of the specified column.
 * It's called by {@link ExcelSheet#analyze(boolean)}.
 */
public abstract class ExcelColumnListener {
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
            throw new IllegalArgumentException("Cant't add Columnlistener to different sheets. Please don't re-use same instance of ExcelColumnListener.");
        }
        this.sheet = sheet;
    }
}
