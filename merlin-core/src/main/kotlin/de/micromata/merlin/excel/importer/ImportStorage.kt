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

import de.micromata.merlin.excel.ExcelWorkbook
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.Validate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*

/**
 * Stores the imported data for displaying and committing.
 *
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
class ImportStorage<T>
@JvmOverloads
constructor(var id: Any? = null,
            excelWorkbook: ExcelWorkbook? = null,
            /**
             * If given, all events of log level or higher will be logged to standard logger (slf4j).
             */
            logLevel: ImportLogger.Level? = null,
            /**
             * Only used as prefix for standard logger (slf4j).
             */
            logPrefix: String? = null,
            /**
             * If given, this logger is used instead of the built-in.
             */
            logger: Logger? = null)
    : Serializable {

    val logger = ImportLogger(excelWorkbook, null, logLevel, logPrefix, logger)

    var workbook: ExcelWorkbook? = null
        set(value) {
            field = value
            logger.excelWorkbook = value
        }

    private var sheets: MutableList<ImportedSheet<T>?>? = null
    /**
     * File name, if data was imported from a file.
     *
     * @return The filename.
     */
    var filename: String? = null

    /**
     * @return the last int given by nextVal without incrementing the underlaying sequencer.
     */
    var lastVal = 0
        private set

    /**
     * Sheets of the import (e. g. mapping of MS Excel sheets).
     *
     * @return the imported sheets.
     */
    fun getSheets(): List<ImportedSheet<T>?>? {
        return sheets
    }

    fun addSheet(sheet: ImportedSheet<T>?) {
        Validate.notNull(sheet)
        if (sheets == null) {
            sheets = ArrayList()
        }
        sheets!!.add(sheet)
    }

    fun getNamedSheet(name: String): ImportedSheet<T>? {
        if (CollectionUtils.isEmpty(sheets) == true) {
            return null
        }
        for (sheet in sheets!!) {
            if (name == sheet!!.name == true) {
                return sheet
            }
        }
        return null
    }

    fun setSheetOpen(name: String, open: Boolean) {
        val sheet = getNamedSheet(name)
        if (sheet != null) {
            sheet.isOpen = open
        } else {
            log.warn("Sheet with name '$name' not found. Can't open/close this sheet in gui.")
        }
    }

    /**
     * Each entry in the sheets (ImportedElements) of the storage should have an unique identifier. Use-age: `new
     * ImportedElement<Xxx>(storage.nextVal(), Xxx.class, ...);` Returns the next integer.
     *
     * @return next value.
     */
    @Synchronized
    fun nextVal(): Int {
        return lastVal++
    }

    companion object {
        private val log = LoggerFactory.getLogger(ImportStorage::class.java)
    }
}
