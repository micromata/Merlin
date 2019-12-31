package de.micromata.merlin.excel.importer

import de.micromata.merlin.excel.ExcelColumnName
import de.micromata.merlin.excel.ExcelSheet
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellReference
import java.io.Serializable

/**
 * For logging events while importing/scanning import files.
 *
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
class ImportLogger
@JvmOverloads constructor(val excelSheet: ExcelSheet? = null)
    : Serializable {
    enum class Level { INFO, WARN, ERROR }
    data class Event(val message: String,
                     val level: Level = Level.INFO,
                     val row: Int? = null, val col: Int? = null) {
        override fun toString(): String {
            return "[$level]$positionString $message"
        }

        val positionString: String
            get() {
                return if (row == null) {
                    if (col == null) ""
                    else " [col=${CellReference.convertNumToColString(col)}]"
                } else if (col == null) {
                    " [row?$row]"
                } else {
                    " [$row, ${CellReference.convertNumToColString(col)}]"
                }
            }
    }

    var successCounter = 0
        private set

    val events = mutableListOf<Event>()

    val eventsAsString: String
        get() = events.joinToString("\n") { it.toString() }

    val infoEvents: List<Event>
        get() = events.filter { it.level == Level.INFO }

    val infoEventsAsString: String
        get() = infoEvents.joinToString("\n") { it.toString() }

    val warnEvents: List<Event>
        get() = events.filter { it.level == Level.WARN }

    val warnEventsAsString: String
        get() = warnEvents.joinToString("\n") { it.toString() }

    val errorEvents: List<Event>
        get() = events.filter { it.level == Level.ERROR }

    val errorEventsAsString: String
        get() = errorEvents.joinToString("\n") { it.toString() }

    val hasEvents: Boolean
        get() = !events.isEmpty()

    val hasErrorEvents: Boolean
        get() = !errorEvents.isEmpty()

    @JvmOverloads
    fun info(message: String, row: Int? = null, col: Int? = null) {
        events.add(Event(message, Level.INFO, row, col))
    }

    fun info(message: String, row: Row, columnName: ExcelColumnName) {
        val col = excelSheet?.getColumnDef(columnName.head)?.columnNumber
        info(message, row.rowNum, col)
    }

    @JvmOverloads
    fun warn(message: String, row: Int? = null, col: Int? = null, markInExcelSheet: Boolean = false) {
        events.add(Event(message, Level.WARN, row, col))
        if (markInExcelSheet && row != null && col != null)
            markError(message, row, col)
    }

    fun warn(message: String, row: Row, columnName: ExcelColumnName, markInExcelSheet: Boolean = false) {
        val col = excelSheet?.getColumnDef(columnName.head)?.columnNumber
        warn(message, row.rowNum, col, markInExcelSheet)
    }

    @JvmOverloads
    fun error(message: String, row: Int? = null, col: Int? = null, markInExcelSheet: Boolean = false) {
        events.add(Event(message, Level.ERROR, row, col))
        if (markInExcelSheet && row != null && col != null)
            markError(message, row, col)
    }

    fun error(message: String, row: Row, columnName: ExcelColumnName, markInExcelSheet: Boolean = false) {
        val col = excelSheet?.getColumnDef(columnName.head)?.columnNumber
        error(message, row.rowNum, col, markInExcelSheet)
    }

    fun addValidationErrors(sheet: ExcelSheet) {
        sheet.allValidationErrors.forEach {
            error(it.message, it.row, it.columnDef.columnNumber)
        }
    }

    private fun markError(msg: String, row: Int, col: Int) {
        println("TBD: mark errors in Excel sheet.")
    }

    fun incrementSuccesscounter() {
        synchronized(this) {
            ++this.successCounter
        }
    }
}
