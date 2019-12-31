package de.micromata.merlin.excel

import org.apache.poi.ss.util.CellReference
import org.slf4j.LoggerFactory

/**
 * You may define each column of a sheet via ExcelColumnDef. This is used as well for validating as well as for a more
 * convenient parsing of Excel values.
 */
class ExcelColumnDef
constructor(val sheet: ExcelSheet,
            var columnHeadname: String?,
            vararg var columnAliases: String) {

    internal constructor(sheet: ExcelSheet, columnNumber: Int, columnHeadname: String?) : this(sheet, columnHeadname) {
        this._columnNumber = columnNumber
        this.columnHeadname = columnHeadname ?: CellReference.convertNumToColString(columnNumber)
    }

    /**
     * If multiple column heads are found (in the head row with same name), occurenceNumber defines the column to select.
     * The first occurence (occurenceNumber = 1) is the default.
     */
    var occurrenceNumber = 1

    val normalizedHeaderName
        get() = normalizedHeaderName(columnHeadname)

    val columnNumber: Int
        get() {
            require(_columnNumber >= 0) { "Column '$columnHeadname' not found. Column number is invalid!" }
            return _columnNumber
        }

    /**
     * Return the number of this column (0-based). The number is set by [ExcelSheet.findAndReadHeadRow].
     */
    internal var _columnNumber = -1

    private var columnListeners: MutableList<ExcelColumnListener>? = null

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
        val normalizedName = normalizedHeaderName(name)
        if (normalizedHeaderName == normalizedName) {
            return true
        }
        columnAliases.forEach {
            if (normalizedHeaderName(it) == normalizedName) {
                log.debug("Column name '$name' matches the alias '$it'.")
                return true
            }
        }
        return false
    }

    fun hasColumnListeners(): Boolean {
        return !columnListeners.isNullOrEmpty()
    }

    fun getColumnListeners(): List<ExcelColumnListener>? {
        return columnListeners
    }

    fun addColumnListener(columnListener: ExcelColumnListener) {
        if (columnListeners == null) {
            columnListeners = mutableListOf()
        }
        val listener = columnListener.with(sheet, this)
        columnListeners!!.add(listener)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ExcelColumnDef::class.java)

        fun normalizedHeaderName(header: String?): String {
            return header?.toLowerCase()?.trim { it <= ' ' } ?: ""
        }
    }
}
