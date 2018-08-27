package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelValidationErrorCommentWriter {
    public void setCellComment(Cell cell, ExcelWriterContext context, ExcelSheet sheet, ExcelColumnDef columnDef,
                               Row row, String comment) {
        PoiHelper.setComment(cell, comment);
    }
}
