package de.micromata.merlin.excel

import org.apache.poi.ss.usermodel.Cell

/**
 * A column listener assigned to a [ExcelColumnDef] listens to all read cell values of the specified column.
 * It's called by [ExcelSheet.analyze].
 */
abstract class ExcelColumnListener {
    var columnDef: ExcelColumnDef? = null
        private set

    var sheet: ExcelSheet? = null
        private set

    abstract fun readCell(cell: Cell?, rowNumber: Int)

    /**
     * Needed for usage in multiple sheets/columns.
     */
    protected abstract fun clone(): ExcelColumnListener

    /**
     * @return this, or, if a columnDef or sheet is already registered a clone of this with the given columnDef.
     */
    internal fun with(sheet: ExcelSheet, columnDef: ExcelColumnDef): ExcelColumnListener {
        val result = if (this.columnDef != null ||this.sheet != null) clone() else this
        result.columnDef = columnDef
        result.sheet = sheet
        return result
    }
}
