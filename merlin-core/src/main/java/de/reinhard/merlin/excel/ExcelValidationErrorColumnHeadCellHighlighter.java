package de.reinhard.merlin.excel;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelValidationErrorColumnHeadCellHighlighter {
    public void highlightColumnHeadCell(Cell cell, ExcelWriterContext context, ExcelSheet sheet, ExcelColumnDef columnDef, Row row) {
        if (row == null) {
            return;
        }
        cell.setCellStyle(context.getErrorHighlightCellStyle());
    }
}
