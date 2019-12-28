package de.micromata.merlin.excel

import org.apache.poi.ss.util.CellReference
import java.util.*

/**
 * You may define each column of a sheet via ExcelColumnDef. This is used as well for validating as well as for a more
 * convenient parsing of Excel values.
 */
class ExcelColumnDef {
    /**
     * Return the number of this column (0-based). The number is set by [ExcelSheet.findAndReadHeadRow].
     */
    var columnNumber = -1
        get() {
            require(columnNumber >= 0) { "Column '$columnHeadname' not found. Column number is invalid!" }
            return columnNumber
        }
        internal set

    /**
     * @return Column head name (1st row) if given, otherwise [.getColumnNumberAsLetters].
     */
    var columnAliases: Array<out String>? = null
    val columnHeadname: String
    private var columnListeners: MutableList<ExcelColumnListener>? = null

    internal constructor(columnHeadname: String, vararg columnAliases: String) {
        this.columnHeadname = columnHeadname
        this.columnAliases = columnAliases
    }

    internal constructor(columnNumber: Int, columnHeadname: String?) {
        this.columnNumber = columnNumber
        this.columnHeadname = columnHeadname ?: CellReference.convertNumToColString(columnNumber)
    }

    fun found(): Boolean {
        return columnNumber >= 0
    }

    /**
     * @return Column number as letters: A, B, ..., AA, AB, ...
     * @see CellReference.convertNumToColString
     */
    val columnNumberAsLetters: String
        get() = CellReference.convertNumToColString(columnNumber)

    /**
     * @return true, if the name (toLowerCase) matches column head or any alias (toLowerCase).
     */
    fun matchName(name: String): Boolean {
        val toLowerName = name.toLowerCase().trim();
        if (columnHeadname.toLowerCase().trim() == toLowerName) {
            return true
        }
        columnAliases?.forEach {
            if (it.toLowerCase().trim() == toLowerName) {
                return true
            }
        }
        return false;
    }

    fun hasColumnListeners(): Boolean {
        return columnListeners != null && columnListeners!!.size > 0
    }

    fun getColumnListeners(): List<ExcelColumnListener>? {
        return columnListeners
    }

    fun addColumnListener(columnListener: ExcelColumnListener) {
        if (columnListeners == null) {
            columnListeners = ArrayList()
        }
        columnListeners!!.add(columnListener)
        columnListener.setColumnDef(this)
    }
}
