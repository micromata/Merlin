package de.reinhard.merlin.excel;

/**
 * Analyses all column values for some statistics.
 */
public class ColumnStatistics implements ColumnListener {
    private int maxLength = 0;

    public void readStringCellValue(String value, int rowNumber) {
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
