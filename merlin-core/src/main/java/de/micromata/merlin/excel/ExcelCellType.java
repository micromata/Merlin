package de.micromata.merlin.excel;

import org.apache.poi.ss.usermodel.CellType;

/**
 * Used for creating new ExcelCells.
 */
public enum ExcelCellType {
    DATE, STRING, INT, DOUBLE, BOOLEAN;

    public CellType getCellType() {
        switch (this) {
            case INT:
            case DOUBLE:
            case DATE:
                return CellType.NUMERIC;
            case BOOLEAN:
                return CellType.BOOLEAN;
            case STRING:
            default:
                return CellType.STRING;
        }

    }
}
