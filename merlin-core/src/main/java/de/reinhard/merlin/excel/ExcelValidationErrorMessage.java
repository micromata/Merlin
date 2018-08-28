package de.reinhard.merlin.excel;

import de.reinhard.merlin.I18n;
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

    /**
     * @param i18n
     * @return Message including sheet name, column and row.
     */
    public String getMessageWithAllDetails(I18n i18n) {
        // 0 - sheet name, 1 - column number as letters, 2 - column name, 3 - row number, 4 - message.
        return i18n.formatMessage("merlin.excel.validation_error.display_all",
                getSheetName(),
                columnDef != null ? columnDef.getColumnNumberAsLetters() : "",
                columnDef != null ? columnDef.getColumnHeadname() : "",
                row + 1,
                getMessage(i18n));
    }

    /**
     * @param i18n
     * @return Message including sheet name, column and row.
     */
    public String getMessageWithSheetName(I18n i18n) {
        // 0 - sheet name, 1 - message.
        return i18n.formatMessage("merlin.excel.validation_error.display_sheet",
                getSheetName(),
                getMessage(i18n));
    }

    /**
     * @param i18n
     * @return Message including column number/name.
     */
    public String getMessageWithColumn(I18n i18n) {
        // 0 - sheet name, 1 - message.
        if (columnDef != null) {
            return i18n.formatMessage("merlin.excel.validation_error.display_column",
                    columnDef.getColumnNumberAsLetters(),
                    columnDef.getColumnHeadname(),
                    getMessage(i18n));
        } else {
            // Column not given. So can't display column.
            return getMessage(i18n);
        }
    }

    public String getMessage(I18n i18n) {
        Object[] params;
        if (cellValue != null) {
            if (getParameters() != null) {
                params = ArrayUtils.addAll(new Object[]{getCellValue()}, getParameters());
            } else {
                params = new Object[]{getCellValue()};
            }
        } else {
            params = getParameters();
        }
        if (params != null && params.length > 0) {
            return i18n.formatMessage(getMessageId(), params);
        } else {
            return i18n.getMessage(getMessageId());
        }
    }

    @Override
    public String getMessage() {
        return getMessage(I18n.getDefault());
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
