package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Analyses all column values for some statistics.
 */
public class ColumnStatistics implements ColumnListener {
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
     * @return the length of the longest string value of all cells in the colum
     */
    public int getMaxLength() {
        return maxLength;
    }
}
