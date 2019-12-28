package de.micromata.merlin.excel

import org.apache.poi.ss.util.CellReference
import org.slf4j.LoggerFactory
import java.util.*

/**
 * You may define each column of a sheet via ExcelColumnDef. This is used as well for validating as well as for a more
 * convenient parsing of Excel values.
 */
class ExcelColumnDef {
    /**
     * If multiple column heads are found (in the head row with same name), occurenceNumber defines the column to select.
     * The first occurence (occurenceNumber = 1) is the default.
     */
    var occurrenceNumber = 1

    val normalizedHeaderName
        get() = normalizedHeaderName(columnHeadname)

    private val log = LoggerFactory.getLogger(ExcelSheet::class.java)
    val columnNumber: Int
        get() {
            require(_columnNumber >= 0) { "Column '$columnHeadname' not found. Column number is invalid!" }
            return _columnNumber
        }

    /**
     * Return the number of this column (0-based). The number is set by [ExcelSheet.findAndReadHeadRow].
     */
    internal var _columnNumber = -1

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
        this._columnNumber = columnNumber
        this.columnHeadname = columnHeadname ?: CellReference.convertNumToColString(columnNumber)
    }

    fun found(): Boolean {
        return _columnNumber >= 0
    }

    /**
     * @return Column number as letters: A, B, ..., AA, AB, ...
     * @see CellReference.convertNumToColString
     */
    val columnNumberAsLetters: String
        get() = CellReference.convertNumToColString(_columnNumber)

    /**
     * @return true, if the name (toLowerCase) matches column head or any alias (toLowerCase).
     */
    fun match(name: String): Boolean {
        val normalizedName = normalizedHeaderName(name);
        if (normalizedHeaderName == normalizedName) {
            return true
        }
        columnAliases?.forEach {
            if (normalizedHeaderName(it) == normalizedName) {
                log.debug("Column name '$name' matches the alias '$it'.")
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

    companion object {
        fun normalizedHeaderName(header: String): String {
            return header.toLowerCase().trim { it <= ' ' }
        }
    }
}
