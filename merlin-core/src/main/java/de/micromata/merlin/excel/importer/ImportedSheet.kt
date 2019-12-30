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
import java.io.Serializable
import java.util.*

/**
 * Represents an imported sheet (e. g. MS Excel sheet) containing the bean objects.
 *
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
class ImportedSheet<T>
@JvmOverloads
constructor(val excelSheet: ExcelSheet? = null)
    : Serializable {
    val logger = ImportLogger()

    private var elements: MutableList<ImportedElement<T>>? = null

    /**
     * Name of the sheet (e. g. name of the MS Excel sheet).
     *
     * @return The name of the sheet.
     */
    var name: String? = null
        set(value) {
            field = value
            if (origName == null) {
                origName = value
            }
        }

    var origName: String? = null
        set(value) {
            field = value
            if (name == null) {
                name = value
            }
        }

    /**
     * Can be used for opening and closing this sheet in gui.
     *
     * @return true if open, otherwise false.
     */
    var isOpen = false
    private var totalNumberOfElements = 0
    private var numberOfNewElements = 0
    private var numberOfModifiedElements = 0
    /**
     * Nur ungleich 0, falls die Datensätze schon verprobt wurden.
     *
     * @return The number of unmodified elements.
     */
    var numberOfUnmodifiedElements = 0
        private set
    var numberOfFaultyElements = 0
        private set
    private var dirty = true
    var isReconciled = false
        private set
    /**
     * After commit, the number of committed values will be given.
     *
     * @return The number of committed elements.
     */
    var numberOfCommittedElements = -1
    private var properties: MutableMap<String, Any>? = null
    private var errorProperties: MutableMap<String?, MutableSet<Any>>? = null
    private var status: ImportStatus? = ImportStatus.NOT_RECONCILED
    /**
     * List of imported elements (e. g. MS Excel rows as bean object).
     *
     * @return The list of imported elements.
     */
    fun getElements(): List<ImportedElement<T>>? {
        return elements
    }

    fun addElement(element: ImportedElement<T>) {
        if (elements == null) {
            elements = mutableListOf()
        }
        elements!!.add(element)
    }

    fun getTotalNumberOfElements(): Int {
        checkStatistics()
        return totalNumberOfElements
    }

    /**
     * Nur ungleich 0, falls die Datensätze schon verprobt wurden.
     *
     * @return The number of new elements.
     */
    fun getNumberOfNewElements(): Int {
        checkStatistics()
        return numberOfNewElements
    }

    fun selectAll(select: Boolean, onlyModified: Boolean) {
        elements?.forEach { element ->
            if (!onlyModified || element.isModified || element.isNew) {
                element.selected = select
            } else {
                element.selected = !select
            }
        }
    }

    fun select(select: Boolean, onlyModified: Boolean, number: Int) {
        var counter = number
        elements?.forEach { element ->
            if (!onlyModified || element.isModified || element.isNew) {
                if (--counter < 0) {
                    element.selected = !select
                } else {
                    element.selected = select
                }
            } else {
                element.selected = !select
            }
        }
    }

    /**
     * Nur ungleich 0, falls die Datensätze schon verprobt wurden.
     *
     * @return The number of modified elements.
     */
    fun getNumberOfModifiedElements(): Int {
        checkStatistics()
        return numberOfModifiedElements
    }

    fun calculateStatistics() {
        totalNumberOfElements = 0
        numberOfNewElements = 0
        numberOfModifiedElements = 0
        numberOfUnmodifiedElements = 0
        numberOfFaultyElements = 0
        var changes = false
        elements?.forEach { element ->
            totalNumberOfElements++
            if (isReconciled) {
                element.isReconciled = true
                when {
                    element.isNew -> {
                        numberOfNewElements++
                        changes = true
                    }
                    element.isModified -> {
                        numberOfModifiedElements++
                        changes = true
                    }
                    element.isUnmodified -> {
                        numberOfUnmodifiedElements++
                    }
                }
            }
            if (element.isFaulty == true) {
                numberOfFaultyElements++
            }
        }
        if (status == ImportStatus.RECONCILED) {
            if (!changes) {
                status = ImportStatus.NOTHING_TODO
            }
        }
        if (isFaulty) {
            status = ImportStatus.HAS_ERRORS
        }
        dirty = false
    }

    private fun checkStatistics() {
        if (dirty) {
            calculateStatistics()
        }
    }

    fun getStatus(): ImportStatus? {
        if (status == null) {
            checkStatistics()
        }
        return status
    }

    fun setStatus(status: ImportStatus) {
        var allowed = true
        if (this.status == ImportStatus.NOT_RECONCILED || this.status == null) {
            if (status.isIn(ImportStatus.IMPORTED, ImportStatus.NOTHING_TODO)) { // State change not allowed.
                allowed = false
            }
        } else if (this.status == ImportStatus.RECONCILED) { // Everything is allowed
        } else { // Everything is allowed
        }
        if (allowed == false) {
            throw UnsupportedOperationException("State change not allowed: '" + this.status + "' -> '" + status + "'")
        }
        this.status = status
        if (status == ImportStatus.RECONCILED) {
            isReconciled = true
        } else if (status == ImportStatus.NOT_RECONCILED) {
            isReconciled = false
        }
        if (isFaulty == true) {
            this.status = ImportStatus.HAS_ERRORS
        }
    }

    val isFaulty: Boolean
        get() = numberOfFaultyElements > 0

    fun setProperty(key: String, value: Any) {
        if (properties == null) {
            properties = HashMap()
        }
        properties!![key] = value
    }

    fun getProperty(key: String?): Any? {
        return if (properties == null) {
            null
        } else properties!![key]
    }

    fun getErrorProperties(): Map<String?, Set<Any?>?>? {
        if (!dirty && errorProperties != null) {
            return errorProperties
        }
        errorProperties = null
        elements?.forEach { el ->
            if (el.isFaulty == true) {
                val map = el.getErrorProperties()
                for (key in map!!.keys) {
                    val value = map[key]!!
                    if (errorProperties == null) {
                        errorProperties = mutableMapOf()
                    }
                    var set: MutableSet<Any>? = null
                    if (errorProperties!!.containsKey(key)) {
                        set = errorProperties!![key]
                    }
                    if (set == null) {
                        set = TreeSet()
                        errorProperties!![key] = set
                    }
                    set.add(value)
                }
            }
        }
        return errorProperties
    }
}
