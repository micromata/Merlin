package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PoiHelper {
    private static Logger log = LoggerFactory.getLogger(PoiHelper.class);
    static String getValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    static Object getValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                return cell.getStringCellValue();
            case BLANK:
                return null;
            default:
                log.warn("Unsupported Excel cell type: " + cell.getCellTypeEnum());
                return getValueAsString(cell);
        }
    }

    static void setComment(Cell cell, String message) {
        Comment actComment = cell.getCellComment();
        if (actComment != null) {
            log.error("Cell comment does already exist. Can't add cell comment twice.");
            return;
        }
        Drawing drawing = cell.getSheet().createDrawingPatriarch();
        CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();

        // When the comment box is visible, have it show in a 1x3 space
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex() + 2);
        anchor.setRow1(cell.getRowIndex());
        anchor.setRow2(cell.getRowIndex() + 2);
        // Create the comment and set the text+author
        Comment comment = drawing.createCellComment(anchor);
        RichTextString str = factory.createRichTextString(message);
        comment.setString(str);
        //comment.setAuthor("Merlin");
        // Assign the comment to the cell
        cell.setCellComment(comment);
    }
}
