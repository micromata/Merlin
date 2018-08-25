package de.reinhard.merlin.excel;

import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.ResultMessageStatus;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ExcelValidationErrorMessage extends ResultMessage implements Comparable {
    private ExcelSheet sheet;
    private int row = 0;
    private ExcelColumnDef columnDef;
    private Object cellValue;

    public ExcelValidationErrorMessage(String messageId, ResultMessageStatus status,
                                       Object... parameters) {
        super(messageId, status, parameters);
    }

    public Object getCellValue() {
        return cellValue;
    }

    @Override
    public Object[] getParameters() {
        return ArrayUtils.addAll(new Object[]{getSheetName(), columnDef.getColumnNumberAsLetters(),
                columnDef.getColumnHeadname(), row + 1, getCellValue()}, super.getParameters());
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

    private String getSheetName() {
        return sheet != null ? sheet.getSheetName() : "";
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExcelValidationErrorMessage)) {
            return false;
        }
        ExcelValidationErrorMessage otherObj = (ExcelValidationErrorMessage) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(getSheet(), otherObj.getSheet());
        builder.append(row, otherObj.row);
        builder.append(getMessageId(), otherObj.getMessageId());
        builder.append(columnDef, otherObj.columnDef);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getSheet());
        builder.append(row);
        builder.append(getMessageId());
        builder.append(columnDef);
        return builder.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        if (this.equals(o)) {
            return 0;
        }
        ExcelValidationErrorMessage other = (ExcelValidationErrorMessage) o;
        int sheetIndex1 = getSheet() != null ? getSheet().getSheetIndex() : -1;
        int sheetIndex2 = other.getSheet() != null ? other.getSheet().getSheetIndex() : -1;
        int columnNumber1 = columnDef != null ? columnDef.getColumnNumber() : -1;
        int columnNumber2 = other.columnDef != null ? other.columnDef.getColumnNumber() : -1;
        return new CompareToBuilder()
                .append(sheetIndex1, sheetIndex2)
                .append(row, other.row)
                .append(columnNumber1, columnNumber2)
                .append(getMessageId(), other.getMessageId())
                .toComparison();
    }
}
