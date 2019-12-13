package de.micromata.merlin.excel;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some helper classes.
 */
public class PoiHelper {
  private static Logger log = LoggerFactory.getLogger(PoiHelper.class);

  public static String getValueAsString(Cell cell) {
    if (cell == null) {
      return null;
    }
    if (cell.getCellType() == CellType.STRING) {
      return cell.getStringCellValue();
    }
    DataFormatter formatter = new DataFormatter();
    return formatter.formatCellValue(cell);
  }

  public static Object getValue(Cell cell) {
    if (cell == null) {
      return null;
    }
    switch (cell.getCellType()) {
      case BOOLEAN:
        return cell.getBooleanCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return cell.getDateCellValue();
        }
        return cell.getNumericCellValue();
      case STRING:
        return cell.getStringCellValue();
      case BLANK:
        return null;
      default:
        log.warn("Unsupported Excel cell type: " + cell.getCellType());
        return getValueAsString(cell);
    }
  }

  public static boolean isEmpty(Cell cell) {
    if (cell == null) {
      return true;
    }
    if (cell.getCellType() == CellType.BLANK) {
      return true;
    }
    if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) {
      return true;
    }
    return false;
  }

  public static void setComment(ExcelCell cell, String message) {
      setComment(cell.getCell(), message);
  }

  public static void setComment(Cell cell, String message) {
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
    anchor.setCol2(cell.getColumnIndex() + 3);
    anchor.setRow1(cell.getRowIndex());
    anchor.setRow2(cell.getRowIndex() + 3);
    // Create the comment and set the text+author
    Comment comment = drawing.createCellComment(anchor);
    RichTextString str = factory.createRichTextString(message);
    comment.setString(str);
    //comment.setAuthor("Merlin");
    // Assign the comment to the cell
    cell.setCellComment(comment);
  }
}
