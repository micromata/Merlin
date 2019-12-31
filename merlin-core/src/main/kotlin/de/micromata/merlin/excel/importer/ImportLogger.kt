package de.micromata.merlin.excel.importer

import de.micromata.merlin.excel.ExcelColumnName
import de.micromata.merlin.excel.ExcelSheet
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellReference
import org.slf4j.LoggerFactory
import java.io.Serializable

/**
 * For logging events while importing/scanning import files.
 *
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
class ImportLogger
@JvmOverloads constructor(val excelSheet: ExcelSheet? = null,
                          /**
                           * If given, all events of log level or higher will be logged to standard logger (slf4j).
                           */
                          val logLevel: Level? = null,
                          /**
                           * Only used as prefix for standard logger (slf4j).
                           */
                          val logPrefix: String? = null)
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
        get() = events.isNotEmpty()

    val hasErrorEvents: Boolean
        get() = errorEvents.isNotEmpty()

    @JvmOverloads
    fun info(message: String, row: Int? = null, col: Int? = null) {
        val event = Event(message, Level.INFO, row, col)
        events.add(event)
        logMessage(event, Level.INFO, log::info)
    }

    @JvmOverloads
    fun info(message: String, row: Row, columnName: ExcelColumnName? = null) {
        val col = if (columnName != null) excelSheet?.getColumnDef(columnName.head)?.columnNumber else null
        info(message, row.rowNum, col)
    }

    @JvmOverloads
    fun warn(message: String, row: Int? = null, col: Int? = null, markInExcelSheet: Boolean = false) {
        val event = Event(message, Level.WARN, row, col)
        events.add(event)
        logMessage(event, Level.WARN, log::info)
        if (markInExcelSheet && row != null && col != null)
            markError(message, row, col)
    }

    fun warn(message: String, row: Row, columnName: ExcelColumnName, markInExcelSheet: Boolean = false) {
        val col = excelSheet?.getColumnDef(columnName.head)?.columnNumber
        warn(message, row.rowNum, col, markInExcelSheet)
    }

    @JvmOverloads
    fun error(message: String, row: Int? = null, col: Int? = null, markInExcelSheet: Boolean = false) {
        val event = Event(message, Level.ERROR, row, col)
        events.add(event)
        logMessage(event, Level.ERROR, log::info)
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

    private fun logMessage(event: Event, level: Level, log: (String) -> Unit) {
        if (logLevel == null || logLevel < level)
            return
        val sb = StringBuilder()
        if (!logPrefix.isNullOrEmpty()) {
            sb.append(logPrefix).append(" ")
        }
        if (excelSheet != null) {
            sb.append("Sheet '")
                    .append(excelSheet.sheetName)
                    .append("': ")
        }
        sb.append(event)
        log(sb.toString())
    }

    companion object {
        private val log = LoggerFactory.getLogger(ImportLogger::class.java)
    }
}
