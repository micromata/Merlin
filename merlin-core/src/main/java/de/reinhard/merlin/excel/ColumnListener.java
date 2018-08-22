package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * A column listener assigned to a {@link ExcelColumnDef} listens to all read cell values.
 */
public abstract class ColumnListener {
    protected ExcelColumnDef columnDef;

    abstract void readCell(Cell cell, int rowNumber);

    void setColumnDef(ExcelColumnDef columnDef) {
        this.columnDef = columnDef;
    }
}
