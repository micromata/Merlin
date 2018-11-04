package de.micromata.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Analyses all column values for some statistics.
 */
public class ExcelColumnAnalyzer extends ExcelColumnListener {
    private int maxLength = 0;

    public void readCell(Cell cell, int rowNumber) {
        String value = PoiHelper.getValueAsString(cell);
        if (value == null) {
            return;
        }
        if (value.length() > maxLength) {
            maxLength = value.length();
        }
    }

    /**
     * @return the length of the longest string value of all cells in the column.
     */
    public int getMaxLength() {
        return maxLength;
    }
}
