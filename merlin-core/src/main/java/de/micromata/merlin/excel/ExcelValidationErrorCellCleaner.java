package de.micromata.merlin.excel;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;

/**
 * Cleans an Excel sheet: gets rid of validation error messages, cell highlighted error cells and comments.
 * This is use-full for downloading an Excel file containing validation errors, fixing it in the same Excel file and
 * uploading it afterwards.
 */
public class ExcelValidationErrorCellCleaner {
    public int clean(ExcelSheet sheet, ExcelWriterContext context) {
        int columnWithErrorMessages = -1;
        Row row = sheet.getHeadRow();
        if (row != null) {
            for (Cell cell : row) {
                cell.getCellStyle().setFillPattern(FillPatternType.NO_FILL);
            }
            Cell lastCell = row.getCell(row.getLastCellNum() - 1);
            if (PoiHelper.getValueAsString(lastCell).startsWith("***")) {
                columnWithErrorMessages = lastCell.getColumnIndex();
                row.removeCell(lastCell);
                row.setHeight((short)-1);
            }
        }
        Iterator<Row> it = sheet.getDataRowIterator();
        while (it.hasNext()) {
            row = it.next();
            for (Cell cell : row) {
                cell.getCellStyle().setFillPattern(FillPatternType.NO_FILL);
                cell.removeCellComment();
            }
            // Remove cell with error messages.
            if (columnWithErrorMessages >= 0) {
                Cell cell = row.getCell(columnWithErrorMessages);
                if (cell != null) {
                    row.removeCell(cell);
                    row.setHeight((short)-1);
                }
            }
        }
        return columnWithErrorMessages;
    }
}
