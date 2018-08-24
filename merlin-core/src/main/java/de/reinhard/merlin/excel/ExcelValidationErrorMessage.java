package de.reinhard.merlin.excel;

import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.ResultMessageStatus;

public class ExcelValidationErrorMessage extends ResultMessage {
    private ExcelSheet sheet;
    private ColumnValidator validator;
    private int row;
    private ExcelColumnDef columnDef;
    private Object cellValue;

    public ExcelValidationErrorMessage(String messageId, ResultMessageStatus status, String message,
                                       Object... parameters) {
        super(messageId, status, message, parameters);
    }

    public Object getCellValue() {
        return cellValue;
    }

    /**
     * @param cellValue
     * @return this for chaining.
     */
    public ExcelValidationErrorMessage setCellValue(Object cellValue) {
        this.cellValue = cellValue;
        return this;
    }

    public ExcelSheet getSheet() {
        return sheet;
    }

    public ExcelValidationErrorMessage setSheet(ExcelSheet sheet) {
        this.sheet = sheet;
        return this;
    }

    public int getRow() {
        return row;
    }

    public ExcelValidationErrorMessage setRow(int row) {
        this.row = row;
        return this;
    }

    public ExcelColumnDef getColumnDef() {
        return columnDef;
    }

    public ExcelValidationErrorMessage setColumnDef(ExcelColumnDef columnDef) {
        this.columnDef = columnDef;
        return this;
    }
}
