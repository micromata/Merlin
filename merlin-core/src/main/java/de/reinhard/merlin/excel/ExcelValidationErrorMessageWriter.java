package de.reinhard.merlin.excel;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class ExcelValidationErrorMessageWriter {
    public void updateOrCreateCell(ExcelWriterContext context, ExcelSheet sheet, int errorMessagesColumnNumber,
                                   Row row, ExcelValidationErrorMessage validationError) {

        Cell cell = row.getCell(errorMessagesColumnNumber);
        if (cell == null) {
            cell = row.createCell(errorMessagesColumnNumber, CellType.STRING);
            cell.setCellStyle(context.getErrorColumnCellStyle());
        }
        String actValue = cell.getStringCellValue();
        String message = validationError.getMessageWithColumn(context.getI18n());
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
