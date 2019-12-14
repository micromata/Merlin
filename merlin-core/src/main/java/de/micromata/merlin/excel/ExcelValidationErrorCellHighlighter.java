package de.micromata.merlin.excel;


/**
 * Highlight validation errors in the analyzed Excel sheet.
 */
public class ExcelValidationErrorCellHighlighter {
    public void highlightErrorCell(ExcelCell cell, ExcelWriterContext context, ExcelSheet sheet, ExcelColumnDef columnDef, ExcelRow row) {
        cell.setCellStyle(context.getErrorHighlightCellStyle());
    }

    public void highlightColumnHeadCell(ExcelCell cell, ExcelWriterContext context, ExcelSheet sheet, ExcelColumnDef columnDef, ExcelRow row) {
        if (row == null) {
            return;
        }
        cell.setCellStyle(context.getErrorHighlightCellStyle());
    }

    public void setCellComment(ExcelCell cell, String comment) {
        PoiHelper.setComment(cell, comment);
    }

}
