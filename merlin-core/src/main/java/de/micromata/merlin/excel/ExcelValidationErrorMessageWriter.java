package de.micromata.merlin.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class ExcelValidationErrorMessageWriter {
    private static final String COLUMN_HEAD_ID = "merlin.excel.validation_error.error_column_headname";

    public void updateOrCreateCell(ExcelWriterContext context, ExcelSheet sheet, int errorMessagesColumnNumber,
                                   Row row, ExcelValidationErrorMessage validationError) {
        if (row == null) {
            // Should only occur in empty sheets.
            return;
        }
        // Add column head of error messages if not yet added:
        Row headRow = sheet.getHeadRow();
        if (headRow != null) {
            Cell columnHeadCell = headRow.getCell(errorMessagesColumnNumber);
            if (columnHeadCell == null) {
                columnHeadCell = createNewCell(context, headRow, errorMessagesColumnNumber);
                addMessage(sheet, headRow, columnHeadCell, "*** "
                        + context.getI18n().getMessage(COLUMN_HEAD_ID) + " ***");
            }
        }
        Cell cell = row.getCell(errorMessagesColumnNumber);
        if (cell == null) {
            cell = createNewCell(context, row, errorMessagesColumnNumber);
        }
        String message = validationError.getMessageWithColumn(context.getI18n());
        addMessage(sheet, row, cell, message);
    }

    private Cell createNewCell(ExcelWriterContext context, Row row, int errorMessagesColumnNumber) {
        Cell cell = row.createCell(errorMessagesColumnNumber, CellType.STRING);
        cell.setCellStyle(context.getErrorColumnCellStyle());
        return cell;
    }

    private void addMessage(ExcelSheet sheet, Row row, Cell cell, String message) {
        String actValue = cell.getStringCellValue();
        if (StringUtils.isBlank(actValue)) {
            cell.setCellValue(message);
        } else {
            //increase row height to accomodate one more line of text:
            float actHeight = row.getHeightInPoints();
            row.setHeightInPoints(actHeight + sheet.getPoiSheet().getDefaultRowHeightInPoints());
            cell.setCellValue(actValue + "\n" + message);
        }
    }
}
