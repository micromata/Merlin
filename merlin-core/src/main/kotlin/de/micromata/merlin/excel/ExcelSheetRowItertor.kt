package de.micromata.merlin.excel

import org.apache.poi.ss.usermodel.Row

internal class ExcelSheetRowIterator(private val sheet: ExcelSheet, private val it: Iterator<Row>) : Iterator<Row> {
    private var row: Row? = null

    override fun hasNext(): Boolean {
        if (row != null) return true
        if (!it.hasNext()) return false
        while (it.hasNext()) {
            val nextRow = it.next()
            if (sheet.isRowEmpty(nextRow))
                continue
            row = nextRow
            break
        }
        return row != null
    }

    override fun next(): Row {
        hasNext() // Need to call this to skip empty rows
        val result = row
        row = null
        return result!!
    }
}
