/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2014 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.merlin.excel.importer

import de.micromata.merlin.excel.ExcelSheet
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
            return "[$level] $message$positionString"
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

    @JvmOverloads
    fun info(message: String, row: Int? = null, col: Int? = null) {
        events.add(Event(message, Level.INFO, row, col))
    }

    @JvmOverloads
    fun warn(message: String, row: Int? = null, col: Int? = null) {
        events.add(Event(message, Level.WARN, row, col))
    }

    @JvmOverloads
    fun error(message: String, row: Int? = null, col: Int? = null) {
        events.add(Event(message, Level.ERROR, row, col))
    }
}
