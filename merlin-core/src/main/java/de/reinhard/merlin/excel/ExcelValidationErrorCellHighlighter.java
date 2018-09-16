package de.reinhard.merlin.excel;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Highlight validation errors in the analyzed Excel sheet.
 */
public class ExcelValidationErrorCellHighlighter {
    public void highlightErrorCell(Cell cell, ExcelWriterContext context, ExcelSheet sheet, ExcelColumnDef columnDef, Row row) {
        cell.setCellStyle(context.getErrorHighlightCellStyle());
    }

    public void highlightColumnHeadCell(Cell cell, ExcelWriterContext context, ExcelSheet sheet, ExcelColumnDef columnDef, Row row) {
        if (row == null) {
            return;
        }
        cell.setCellStyle(context.getErrorHighlightCellStyle());
    }

    public void setCellComment(Cell cell, ExcelWriterContext context, ExcelSheet sheet, ExcelColumnDef columnDef,
                               Row row, String comment) {
        PoiHelper.setComment(cell, comment);
    }

}
