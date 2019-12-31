package de.micromata.merlin.excel

import de.micromata.merlin.excel.PoiHelper.getValueAsString
import org.apache.poi.ss.usermodel.Cell

/**
 * Analyses all column values for some statistics.
 */
class ExcelColumnAnalyzer : ExcelColumnListener() {
    /**
     * @return the length of the longest string value of all cells in the column.
     */
    var maxLength = 0
        private set

    override fun clone(): ExcelColumnAnalyzer {
        return ExcelColumnAnalyzer()
    }

    override fun readCell(cell: Cell?, rowNumber: Int) {
        val value = getValueAsString(cell) ?: return
        if (value.length > maxLength) {
            maxLength = value.length
        }
    }

}
