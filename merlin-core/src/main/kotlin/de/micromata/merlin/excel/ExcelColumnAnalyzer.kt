package de.micromata.merlin.excel

import de.micromata.merlin.excel.PoiHelper.getValueAsString
import org.apache.poi.ss.usermodel.Cell

/**
 * Analyses all column values for some statistics.
 */
class ExcelColumnAnalyzer
@JvmOverloads constructor(maxLength: Int = 0) : ExcelColumnListener() {
    /**
     * @return the length of the longest string value of all cells in the column.
     */
    var maxLength = maxLength
        private set

    override fun clone(): ExcelColumnAnalyzer {
        return ExcelColumnAnalyzer(maxLength)
    }

    override fun readCell(cell: Cell?, rowNumber: Int) {
        val value = getValueAsString(cell) ?: return
        if (value.length > maxLength) {
            maxLength = value.length
        }
    }

}
