package de.micromata.merlin.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.zip.Adler32;

/**
 * Helper for building checksums of Excel workbooks. Can be used to detect modifications of Excel files.
 */
public class ExcelChecksum {
    /**
     *
     * @param workbook
     * @param cellToIgnore If given, this cell is ignored. This is mostly the cell containing the checksum itself.
     * @return
     */
    public long create(Workbook workbook, CellLocation cellToIgnore) {
        Adler32 cs = new Adler32();
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cellToIgnore != null
                            && row.getRowNum() == cellToIgnore.rowNum
                            && cell.getColumnIndex() == cellToIgnore.columnIndex
                            && StringUtils.equals(sheet.getSheetName(), cellToIgnore.sheetName)) {
                        continue;
                    }
                    String str = StringUtils.defaultString(cell.getStringCellValue());
                    cs.update(str.getBytes());
                }
            }
        }
        return cs.getValue();
    }

    public class CellLocation {
        int rowNum;
        int columnIndex;
        String sheetName;

        public CellLocation(String sheetName, int rowNum, int columnIndex) {
            this.sheetName = sheetName;
            this.rowNum = rowNum;
            this.columnIndex = columnIndex;
        }
    }
}

