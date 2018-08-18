package de.reinhard.merlin.excel;

/**
 * A column listener assigned to a {@link ExcelColumnDef} listens to all read cell values.
 */
public interface ColumnListener {
    public void readStringCellValue(String cellValue, int rowNumber);
}
