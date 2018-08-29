package de.reinhard.merlin.excel;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelValidationErrorCellHighlighter {
    public void highlightErrorCell(Cell cell, ExcelWriterContext context, ExcelSheet sheet, ExcelColumnDef columnDef, Row row) {
        cell.setCellStyle(context.getErrorHighlightCellStyle());
    }

    public void cleanErrorCell(Cell cell) {

    }
}
