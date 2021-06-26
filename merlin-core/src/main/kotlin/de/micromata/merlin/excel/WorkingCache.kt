package de.micromata.merlin.excel

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Used for caching some stuff for better performance especially for large sheets to create.
 */
internal class WorkingCache {
    internal class Property(val field: Field? = null, val getter: Method? = null)
    internal class AutoFillCache {
        val foundPropertiesMap = mutableMapOf<ExcelColumnDef, Property>()
        val notFoundPropertiesSet = mutableSetOf<ExcelColumnDef>()
    }

    internal val autoFillCache = AutoFillCache()
}
