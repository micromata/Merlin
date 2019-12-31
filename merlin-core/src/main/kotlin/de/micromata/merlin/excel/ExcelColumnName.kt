package de.micromata.merlin.excel

/**
 * For registering Excel columns for usage in enums:
 * Example:
 *
 *     enum class Cols(override val head: String, override vararg val aliases: String) : ExcelColumnName {
 *       DATE("Date", "D"),
 *       ADDRESS("Address", "Addr", "Addr.),...
 *     }
 *
 * A lot of methods supports [ExcelColumnName] as parameter for specifying columns:
 *
 *     registerColumns(DATE, ADDRESS)
 */
interface ExcelColumnName {
    val head: String
    val aliases: Array<out String>
}
