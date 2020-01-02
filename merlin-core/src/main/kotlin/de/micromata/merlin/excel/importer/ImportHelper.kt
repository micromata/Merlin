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
import de.micromata.merlin.excel.PoiHelper
import de.micromata.merlin.utils.BeanHelper

/**
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
object ImportHelper {
    /**
     * Try to copy registered cell values of the given Excel row by name. This works for base data types.
     * Columns should be registered and have a targetProperty which fits the given bean.
     * Ignores null values.
     * @param locale The locale is used to format numbers as Strings (if numbers will be written to string properties of beans).
     * @see de.micromata.merlin.excel.ExcelColumnDef.targetProperty
     */
    @JvmOverloads
    fun fillBean(bean: Any, sheet: ExcelSheet, row: Int) {
        sheet.columnDefinitions.forEach { columnDef ->
            if (columnDef.found()) {
                val targetProperty = columnDef.targetProperty
                val cell = sheet.getCell(row, columnDef)
                if (targetProperty != null && cell != null) {
                    val setter = BeanHelper.determineSetter(bean::class.java, targetProperty)
                    if (setter != null) {
                        val type = setter.parameterTypes[0]
                        val cellValue = if (type?.isAssignableFrom(String::class.java) == true)
                            PoiHelper.getValueAsString(cell, sheet.locale, true)
                        else
                            PoiHelper.getValue(cell)
                        BeanHelper.setProperty(bean, setter, type, cellValue, true)
                    }
                }
            }
        }
    }
}
