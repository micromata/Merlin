package de.micromata.merlin.excel

import org.apache.poi.ss.usermodel.Cell

/**
 * A column listener assigned to a [ExcelColumnDef] listens to all read cell values of the specified column.
 * It's called by [ExcelSheet.analyze].
 *
 * If an ExcelColumnListener is re-used for other sheets or columns, it will automatically cloned.
 */
abstract class ExcelColumnListener {
    var columnDef: ExcelColumnDef? = null
        private set

    /**
     * @return The sheet assigned to the columnDef, if any.
     */
    val sheet: ExcelSheet?
        get() = columnDef?.sheet

    abstract fun readCell(cell: Cell?, rowNumber: Int)

    /**
     * Needed for usage in multiple sheets/columns.
     */
    protected abstract fun clone(): ExcelColumnListener

    /**
     * @return this, or, if a columnDef or sheet is already registered a clone of this with the given columnDef.
     */
    internal fun with(columnDef: ExcelColumnDef): ExcelColumnListener {
        val result = if (this.columnDef != null) clone() else this
        if (this != result && !result.javaClass.isAssignableFrom(this.javaClass)) {
            throw IllegalStateException("Error in clone method: ${this.javaClass}.clone() must not return different object class: ${result.javaClass}. Please implement clone() class correctly.")
        }
        result.columnDef = columnDef
        return result
    }
}
