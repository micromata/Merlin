package de.micromata.merlin.excel

import java.lang.reflect.Field

/**
 * Used for caching some stuff for better performance especially for large sheets to create.
 */
internal class WorkingCache {
    internal class AutoFillCache{
        val foundFieldsMap = mutableMapOf<ExcelColumnDef, Field>()
        val notFoundFieldsSet = mutableSetOf<ExcelColumnDef>()
    }
    internal val autoFillCache = AutoFillCache()
}
