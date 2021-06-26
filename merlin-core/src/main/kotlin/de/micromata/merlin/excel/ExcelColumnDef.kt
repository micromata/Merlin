package de.micromata.merlin.excel

import org.apache.poi.ss.util.CellReference
import org.slf4j.LoggerFactory
import java.util.*

/**
 * You may define each column of a sheet via ExcelColumnDef. This is used as well for validating as well as for a more
 * convenient parsing of Excel values.
 */
class ExcelColumnDef
constructor(
    val sheet: ExcelSheet,
    var columnHeadname: String?,
    vararg var columnAliases: String
) {

    internal constructor(sheet: ExcelSheet, columnNumber: Int, columnHeadname: String?) : this(sheet, columnHeadname) {
        this._columnNumber = columnNumber
        this.columnHeadname = columnHeadname ?: CellReference.convertNumToColString(columnNumber)
    }

    var width: Int? = null

    /**
     * If multiple column heads are found (in the head row with same name), occurenceNumber defines the column to select.
     * The first occurence (occurenceNumber = 1) is the default.
     */
    var occurrenceNumber = 1

    /**
     * If set, validation errors might be attached in [ImportedElement] and automatically assignment to beans is supported.
     * If not given, [columnHeadname] in decapitalized form will be used.
     */
    var targetProperty: String? = null
        get() = if (field != null) field else columnHeadname?.replaceFirstChar { it.lowercase() }
        private set

    /**
     * @return this for chaining.
     * @see [targetProperty]
     */
    fun setTargetProperty(targetProperty: String): ExcelColumnDef {
        this.targetProperty = targetProperty
        return this
    }

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

    @Suppress("UNCHECKED_CAST")
    val columnValidators: List<ExcelColumnValidator>?
        get() = columnListeners?.filter { it is ExcelColumnValidator } as? List<ExcelColumnValidator>

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
        val listener = columnListener.with(this)
        columnListeners!!.add(listener)
    }

    /**
     * Sets width of this cell as size * 256.
     * @param size width / 256
     * @return this for chaining.
     */
    fun withSize(size: Int): ExcelColumnDef {
        width = size * 256
        return this
    }

    override fun toString(): String {
        val sb = StringBuilder()
        addFieldValue(sb, "head", this.columnHeadname)
        if (this.columnAliases.isNotEmpty()) {
            addFieldValue(sb, "alias", this.columnAliases.joinToString("; "))
        }
        if (this.occurrenceNumber > 1) {
            addFieldValue(sb, "occurenceNumber", this.occurrenceNumber)
        }
        if (this._columnNumber >= 0) {
            addFieldValue(sb, "col", CellReference.convertNumToColString(this._columnNumber))
        }
        val validators = this.columnValidators
        if (!validators.isNullOrEmpty()) {
            addFieldValue(sb, "validator", validators.joinToString(", ") { it.javaClass.simpleName })
        }
        return sb.toString()
    }

    private fun addFieldValue(sb: StringBuilder, field: String, value: Any?) {
        if (value == null) return
        if (sb.length > 0)
            sb.append(", ")
        sb.append(field).append("=")
        if (value is String)
            sb.append("\"").append(value).append("\"")
        else
            sb.append(value)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ExcelColumnDef::class.java)

        fun normalizedHeaderName(header: String?): String {
            return header?.lowercase()?.trim { it <= ' ' } ?: ""
        }
    }
}
