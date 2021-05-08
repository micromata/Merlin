package de.micromata.merlin.excel

import de.micromata.merlin.CoreI18n
import de.micromata.merlin.ResultMessageStatus
import de.micromata.merlin.data.Data
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.CellReference
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Wraps and enhances a POI sheet.
 */
class ExcelSheet internal constructor(val excelWorkbook: ExcelWorkbook, val poiSheet: Sheet) {
    private val columnDefList: MutableList<ExcelColumnDef> = ArrayList()

    /**
     * You may prevent info logs about multiple registration of columns with same name.
     */
    var enableMultipleColumns = false

    /**
     * If true, any requested cell will be created if not exists. Default is false.
     * @see [getCell]
     */
    var writeMode: Boolean = false

    val locale: Locale
        get() = excelWorkbook.locale

    @Suppress("unused")
    val columnDefinitions
        get() = columnDefList.toList()

    init {
        log.debug("Reading sheet '" + poiSheet.sheetName + "'")
    }

    /**
     * If true, all cell values will be trimmed before getting the cell value as string. The Excel cell itself will
     * not be modified. Default is false.
     * See: [getCellString]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var autotrimCellValues: Boolean = false
    private var _headRow: ExcelRow? = null
    val headRow: ExcelRow?
        get() {
            findAndReadHeadRow()
            return _headRow
        }

    private var columnWithValidationErrorMessages = -1
    private var validationErrors: MutableSet<ExcelValidationErrorMessage>? = null

    /**
     * @return true, if this sheet was modified (by calling [markErrors].
     */
    var isModified = false
    private val i18n = CoreI18n.getDefault()
    private var maxMarkedErrors = 100
    private val excelRowMap: MutableMap<Int, ExcelRow> = HashMap()

    private var rowEmptyColumns: IntArray? = null

    /**
     * Forces reloading of head row. This is useful if you get the head row, read it manually to add new
     * [ExcelColumnDef] objects and to restart.
     */
    fun reset() {
        _headRow = null
        isModified = false
        validationErrors = null
    }

    /**
     * Analyzes sheet.
     *
     *
     * Each cell will be analyzed by calling ExcelColumnListener for each column with given
     * ExcelColumnListener. If no Analyzer is set for a column, the column cells will not be analyzed.
     *
     * @param validate if true, then each cell of a column with a given ExcelColumnValidator will be validated.
     * @return this for chaining.
     */
    fun analyze(validate: Boolean): ExcelSheet {
        findAndReadHeadRow()
        if (validate) { // Detect missing columns:
            for (columnDef in columnDefList) {
                if (!columnDef.hasColumnListeners()) {
                    continue
                }
                for (listener in columnDef.getColumnListeners()!!) {
                    if (listener is ExcelColumnValidator) {
                        if (columnDef._columnNumber < 0) {
                            addValidationError(createValidationErrorMissingColumnByName(columnDef.columnHeadname))
                        }
                    }
                }
            }
        }
        val it = dataRowIterator
        while (it.hasNext()) {
            val row = it.next()
            if (row.lastCellNum > columnWithValidationErrorMessages) {
                columnWithValidationErrorMessages = row.lastCellNum.toInt()
            }
            for (columnDef in columnDefList) {
                if (!columnDef.hasColumnListeners() || columnDef._columnNumber < 0) {
                    continue
                }
                for (listener in columnDef.getColumnListeners()!!) {
                    if (listener !is ExcelColumnValidator || validate) {
                        val cell = row.getCell(columnDef._columnNumber)
                        listener.readCell(cell, row.rowNum)
                    }
                }
            }
        }
        return this
    }

    /**
     * @param columnHeads The column heads to register.
     * @return this for chaining.
     */
    @Suppress("unused")
    @JvmOverloads
    fun registerColumns(vararg columnHeads: ExcelColumnName, listener: ExcelColumnListener? = null): ExcelSheet {
        for (name in columnHeads) {
            registerColumn(name.head, *name.aliases, listener = listener)
        }
        return this
    }

    /**
     * @param columnHeads The column heads to register.
     * @return this for chaining.
     */
    @JvmOverloads
    fun registerColumns(vararg columnHeads: String, listener: ExcelColumnListener? = null): ExcelSheet {
        for (columnHead in columnHeads) {
            registerColumn(columnHead, listener = listener)
        }
        return this
    }

    /**
     * @param columnHead The column head to register.
     * @return Created and registered ExcelColumnDef.
     */
    fun registerColumn(columnHead: String, listener: ExcelColumnListener? = null): ExcelColumnDef {
        @Suppress("RemoveRedundantSpreadOperator")
        return registerColumn(columnHead, *emptyArray(), listener = listener)
    }

    /**
     * @param columnHead The column head to register.
     * @param aliases Sometimes table heads changed for different imports. Register any used aliases for detecting the column by aliases too.
     * @return Created and registered [ExcelColumnDef].
     */
    @JvmOverloads
    fun registerColumn(
        columnHead: String,
        vararg aliases: String,
        listener: ExcelColumnListener? = null
    ): ExcelColumnDef {
        val columnDef = ExcelColumnDef(this, columnHead, *aliases)
        val existing = getColumnDefs(columnHead)
        if (!existing.isEmpty()) {
            columnDef.occurrenceNumber = existing.last().occurrenceNumber + 1
            if (!enableMultipleColumns) {
                log.info("Multiple registration of column head '$columnHead': #${columnDef.occurrenceNumber}")
            }
        }
        if (listener != null) {
            columnDef.addColumnListener(listener)
        }
        columnDefList.add(columnDef)
        return columnDef
    }

    /**
     * @param columnHead The column head to register.
     * @param listener   The listener to use.
     * @return Created and registered [ExcelColumnDef].
     */
    @Suppress("unused")
    @JvmOverloads
    fun registerColumn(columnHead: ExcelColumnName, listener: ExcelColumnListener? = null): ExcelColumnDef {
        return registerColumn(columnHead.head, *columnHead.aliases, listener = listener)
    }

    /**
     * @param columnDef The column to register.
     * @param listener  The listener to use.
     * @return this for chaining.
     */
    @Suppress("unused")
    @JvmOverloads
    fun registerColumn(columnDef: ExcelColumnDef, listener: ExcelColumnListener? = null): ExcelSheet {
        if (listener != null) {
            columnDef.addColumnListener(listener)
        }
        return this
    }

    /**
     * @return Iterator for rows. Iterator starts with data row (head row + 1).
     */
    val dataRowIterator: ExcelSheetRowIterator
        get() {
            findAndReadHeadRow()
            val it = poiSheet.rowIterator()
            if (headRow == null)
                return ExcelSheetRowIterator(this, it)
            while (it.hasNext()) {
                if (it.next() == headRow!!.row) {
                    break
                }
            }
            return ExcelSheetRowIterator(this, it)
        }

    fun readRow(row: Row, data: Data) {
        findAndReadHeadRow()
        for (columnDef in columnDefList) {
            if (row.lastCellNum > columnDef._columnNumber && columnDef._columnNumber >= 0) {
                val cell = row.getCell(columnDef._columnNumber)
                val value = PoiHelper.getValueAsString(cell)
                data.put(columnDef.columnHeadname, value)
            }
        }
    }

    /**
     * @param row         The row to get the cell value from.
     * @param columnName  The name of the column to get.
     * @param nullAsEmpty If true, null cell
     * @param trimValue   If true, the returned value will be trimmed, default is [autotrimCellValues].
     * @return The String value of the specified column cell.
     */
    @JvmOverloads
    fun getCellString(
        row: Row,
        columnName: ExcelColumnName,
        nullAsEmpty: Boolean = true,
        trimValue: Boolean = autotrimCellValues
    ): String? {
        return getCellString(row, columnName.head, nullAsEmpty, trimValue)
    }

    /**
     * @param row            The row to get the cell value from.
     * @param columnHeadname The name of the column to get.
     * @param nullAsEmpty    If true, null cell
     * @param trimValue      If true, the returned value will be trimmed, default is [autotrimCellValues].
     * @return The String value of the specified column cell.
     */
    @JvmOverloads
    fun getCellString(
        row: Row,
        columnHeadname: String,
        nullAsEmpty: Boolean = true,
        trimValue: Boolean = autotrimCellValues
    ): String? {
        // findAndReadHeadRow(); Will be called in getColumnDef
        val cell = getCell(row, columnHeadname, false) ?: return if (nullAsEmpty) "" else null
        return PoiHelper.getValueAsString(cell, locale, trimValue)
    }

    /**
     * @param row         The row to get the cell value from.
     * @param columnDef   The column to get.
     * @param nullAsEmpty If true, null cell
     * @param trimValue   If true, the returned value will be trimmed, default is [autotrimCellValues].
     * @return The String value of the specified column cell.
     */
    fun getCellString(
        row: Row,
        columnDef: ExcelColumnDef?,
        nullAsEmpty: Boolean = true,
        trimValue: Boolean = autotrimCellValues
    ): String? {
        // findAndReadHeadRow(); Will be called in getColumnDef
        val cell = getCell(row, columnDef, false) ?: return if (nullAsEmpty) "" else null
        return PoiHelper.getValueAsString(cell, locale, trimValue)
    }

    /**
     * @param row        The row to get the cell value from.
     * @param columnName The name of the column to get.
     * @return The String value of the specified column cell.
     */
    @Suppress("unused")
    fun getCellInt(
        row: Row,
        columnName: ExcelColumnName
    ): Int? { // findAndReadHeadRow(); Will be called in getColumnDef
        return getCellInt(row, columnName.head)
    }

    /**
     * @param row            The row to get the cell value from.
     * @param columnHeadname The name of the column to get.
     * @return The String value of the specified column cell.
     */
    fun getCellInt(row: Row, columnHeadname: String): Int? { // findAndReadHeadRow(); Will be called in getColumnDef
        return getCellDouble(row, columnHeadname)?.toInt() ?: return null
    }

    /**
     * @param row        The row to get the cell value from.
     * @param columnName The name of the column to get.
     * @return The String value of the specified column cell.
     */
    @Suppress("unused")
    fun getCellDouble(
        row: Row,
        columnName: ExcelColumnName
    ): Double? { // findAndReadHeadRow(); Will be called in getColumnDef
        return getCellDouble(row, columnName.head)
    }

    /**
     * @param row            The row to get the cell value from.
     * @param columnHeadname The name of the column to get.
     * @return The String value of the specified column cell.
     */
    fun getCellDouble(
        row: Row,
        columnHeadname: String
    ): Double? { // findAndReadHeadRow(); Will be called in getColumnDef
        return getNumericCell(row, columnHeadname)?.numericCellValue ?: return null
    }

    private fun getNumericCell(row: Row, columnHeadname: String): Cell? {
        val cell = getCell(row, columnHeadname, false) ?: return null
        if (cell.cellType != CellType.NUMERIC) {
            log.warn("Cell of column '$columnHeadname' in row ${row.rowNum} of sheet '$sheetName' isn't of type numeric: '${cell}'.")
            return null
        }
        return cell
    }

    /**
     * @param row        The row to get the cell from.
     * @param columnName The name of the column to get the cell from.
     * @return The cell of the specified column of the current row (uses internal interator).
     */
    @JvmOverloads
    fun getCell(
        row: Row,
        columnName: ExcelColumnName,
        ensureCell: Boolean = writeMode
    ): Cell? { // findAndReadHeadRow(); Will be called in getColumnDef
        return getCell(row, columnName.head, ensureCell)
    }

    /**
     * @param row            The row to get the cell from.
     * @param columnHeadname The name of the column to get the cell from.
     * @return The cell of the specified column of the current row (uses internal interator).
     */
    @JvmOverloads
    fun getCell(
        row: Row,
        columnHeadname: String,
        ensureCell: Boolean = writeMode
    ): Cell? { // findAndReadHeadRow(); Will be called in getColumnDef
        val columnDef = getColumnDef(columnHeadname)
        return getCell(row, columnDef, ensureCell)
    }

    /**
     * @param row       The row to get the cell from.
     * @param columnDef The specified column to get the cell from.
     * @param ensureCell If true, the cell will be created if not exist. True is default on [writeMode].
     * @return The cell of the specified column of the current row (uses internal interator).
     */
    @JvmOverloads
    fun getCell(row: Row, columnDef: ExcelColumnDef?, ensureCell: Boolean = writeMode): Cell? {
        findAndReadHeadRow()
        if (columnDef == null) {
            return null
        }
        if (columnDef._columnNumber < 0) {
            log.debug("Column '" + columnDef.columnHeadname + "' not found in sheet '" + sheetName + "': can't run cell.")
            return null
        }
        return getCell(row.rowNum, columnDef._columnNumber, ensureCell)
    }

    /**
     * @param row        Excel row number (starting with 0, POI row number).
     * @param columnDef  The specified column to get the cell from.
     * @param ensureCell If true, the cell will be created if not exist. True is default on [writeMode].
     * @return The specified cell.
     */
    @JvmOverloads
    fun getCell(row: Int, columnDef: ExcelColumnDef, ensureCell: Boolean = writeMode): Cell? {
        findAndReadHeadRow()
        return getCell(row, columnDef._columnNumber, ensureCell)
    }

    /**
     * @param rowNum       Excel row number (starting with 0, POI row number).
     * @param columnNumber The specified column to get the cell from.
     * @param ensureCell If true, the cell will be created if not exist. True is default on [writeMode].
     * @return The specified cell.
     */
    @JvmOverloads
    fun getCell(rowNum: Int, columnNumber: Int, ensureCell: Boolean = writeMode): Cell? {
        findAndReadHeadRow()
        return if (ensureCell)
            getRow(rowNum).getCell(columnNumber).cell
        else
            poiSheet.getRow(rowNum)?.getCell(columnNumber)
    }

    internal fun findAndReadHeadRow() {
        if (_headRow != null) {
            return  // head row already run.
        }
        log.debug("Reading head row of sheet '" + poiSheet.sheetName + "'.")
        val rowIterator = poiSheet.rowIterator()
        var current: Row? = null
        for (i in 0..9) { // Detect head row, check Row 0-9 for column heads.
            if (!rowIterator.hasNext()) {
                break
            }
            log.debug("Parsing row #" + i + " of sheet '" + poiSheet.sheetName + "'.")
            current = rowIterator.next()
            if (current.lastCellNum > columnWithValidationErrorMessages) {
                columnWithValidationErrorMessages = current.lastCellNum.toInt()
            }
            var col = -1
            for (cell in current) {
                ++col
                val cellVal = PoiHelper.getValueAsString(cell)
                log.debug("Reading cell '$cellVal' in column $col")
                if (!cellVal.isNullOrBlank() && _getColumnDef(cellVal) != null) {
                    log.debug("Head column found: '$cellVal' in col #$col")
                    _headRow = ensureRow(current)
                    break
                }
            }
            if (_headRow != null) {
                break
            }
        }
        if (_headRow == null || current == null) {
            log.debug("No head row found in sheet '$sheetName'.")
            return
        }
        // Now run all columns for assigning column numbers to column definitions.
        val occurrenceMap = mutableMapOf<String, Int>()
        for (cell in current) {
            val strVal = PoiHelper.getValueAsString(cell)
            if (strVal.isNullOrBlank())
                continue
            log.debug("Reading head column '$strVal' in column ${cell.columnIndex}")
            val normalizedHeaderName = ExcelColumnDef.normalizedHeaderName(strVal)
            val occurrenceNumber = occurrenceMap[normalizedHeaderName] ?: 1
            occurrenceMap[normalizedHeaderName] = occurrenceNumber + 1
            val columnDef = _getColumnDef(strVal, occurrenceNumber)
            if (columnDef != null) {
                log.debug("Head column found: '$strVal' in col #${cell.columnIndex}")
                columnDef._columnNumber = cell.columnIndex
                columnDef.width?.let { setColumnWidth(columnDef, it) }
            } else {
                log.debug("Head column not registered: '$strVal'.")
            }
        }
    }

    @Suppress("unused")
    fun getColumnDef(columnName: ExcelColumnName): ExcelColumnDef? {
        return getColumnDef(columnName.head)
    }

    /**
     * @param identifier Name of head col or alias.
     * @param occurrenceNumber For multiple heads (with name column name), specify the desired number. But it's recommended to use aliases instead.
     */
    @JvmOverloads
    fun getColumnDef(identifier: String, occurrenceNumber: Int = -1): ExcelColumnDef? {
        findAndReadHeadRow()
        return _getColumnDef(identifier, occurrenceNumber)
    }

    /**
     * @param identifier Name of head col or alias.
     * @param occurrenceNumber For multiple heads (with name column name), specify the desired number. But it's recommended to use aliases instead.
     */
    fun _getColumnDef(identifier: String, occurrenceNumber: Int = -1): ExcelColumnDef? {
        if (identifier.isEmpty()) {
            return null
        }
        for (columnDef in columnDefList) {
            if (columnDef.match(identifier)) {
                if (occurrenceNumber < 0 || occurrenceNumber == columnDef.occurrenceNumber) {
                    log.debug("Column '$identifier' found.")
                    return columnDef
                } else {
                    log.debug("Skipping of '$identifier' in col #${columnDef._columnNumber}. Looking further for occurence #$occurrenceNumber")
                }
            }
        }
        log.debug("Column definition '$identifier' not found.")
        return null
    }

    /**
     * @return list of column defs matching the given identifier.
     */
    fun getColumnDefs(identifier: String): List<ExcelColumnDef> {
        if (identifier.isEmpty()) {
            return emptyList()
        }
        return columnDefList.filter { it.match(identifier) }
    }

    @Suppress("unused")
    fun getColumnDef(columnNumber: Int): ExcelColumnDef? {
        for (columnDef in columnDefList) {
            if (columnNumber == columnDef._columnNumber) {
                return columnDef
            }
        }
        return null
    }

    /**
     * @param identifier Name of head col or alias.
     * @param occurrenceNumber For multiple heads (with name column name), specify the desired number. But it's recommended to use aliases instead.
     */
    @Suppress("unused")
    @JvmOverloads
    fun getColNumber(identifier: String, occurrenceNumber: Int = -1): Int? {
        return getColumnDef(identifier, occurrenceNumber)?.columnNumber
    }

    val sheetName: String
        get() = poiSheet.sheetName

    val sheetIndex: Int
        get() = poiSheet.workbook.getSheetIndex(poiSheet)

    /**
     * Don't forget to call analyze(true) first.
     * Is an alias for ![.hasValidationErrors].
     *
     * @return true if no error messages exist, otherwise false.
     */
    val isValid: Boolean
        get() = !hasValidationErrors()

    /**
     * Don't forget to call analyze(true) first.
     *
     * @return true if no error messages exist, otherwise false.
     */
    fun hasValidationErrors(): Boolean {
        if (validationErrors != null && validationErrors!!.size > 0) {
            return true
        }
        for (columnDef in columnDefList) {
            if (!columnDef.hasColumnListeners()) {
                continue
            }
            for (columnListener in columnDef.getColumnListeners()!!) {
                if (columnListener !is ExcelColumnValidator) {
                    continue
                }
                if (columnListener.hasValidationErrors()) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * @return All validation errors of this sheet including all validation errors of all registered [ExcelColumnValidator].
     * An empty set will be returned if no validation error was found.
     */
    val allValidationErrors: Set<ExcelValidationErrorMessage>
        get() {
            val allValidationErrors = mutableSetOf<ExcelValidationErrorMessage>()
            if (validationErrors != null) {
                allValidationErrors.addAll(validationErrors!!)
            }
            for (columnDef in columnDefList) {
                if (!columnDef.hasColumnListeners()) {
                    continue
                }
                columnDef.columnValidators?.forEach { columnValidator ->
                    if (columnValidator.hasValidationErrors()) {
                        allValidationErrors.addAll(columnValidator.validationErrors)
                    }
                }
            }
            return allValidationErrors
        }

    /**
     * Marks and comments validation errors of cells of this sheet by manipulating the Excel sheet.
     * Refer [isModified] for checking if any modification was done.
     * Please don't forget to call [analyze] first with parameter validate=true.
     *
     * @param i18n               For localizing messages.
     * @param excelWriterContext Defines the type of response (how to display and highlight validation errors).
     * @return this for chaining.
     */
    @JvmOverloads
    fun markErrors(excelWriterContext: ExcelWriterContext = ExcelWriterContext(i18n, excelWorkbook)): ExcelSheet {
        columnWithValidationErrorMessages = excelWriterContext.cellCleaner.clean(this, excelWriterContext)
        if (columnWithValidationErrorMessages < 0) {
            log.warn("Can't add error messages, no head row found.")
            return this
        }
        analyze(true)
        val highlightedColumnHeads: MutableSet<ExcelColumnDef> = HashSet()
        var errorCount = 0
        for (validationError in allValidationErrors) {
            if (maxMarkedErrors >= 0 && ++errorCount > maxMarkedErrors) { // Maximum number of errors to mark is exceeded.
                break
            }
            val columnDef = validationError.columnDef
            val row = getRow(validationError.row)
            if (excelWriterContext.isAddErrorColumn) {
                excelWriterContext.errorMessageWriter.updateOrCreateCell(
                    excelWriterContext, this,
                    columnWithValidationErrorMessages, row, validationError
                )
                isModified = true
            }
            if (columnDef != null) {
                val cell = row.getCell(columnDef._columnNumber, ExcelCellType.STRING)
                if (excelWriterContext.isHighlightErrorCells) { // Cell validation error. Highlight cell.
                    excelWriterContext.cellHighlighter.highlightErrorCell(
                        cell, excelWriterContext, this,
                        columnDef, row
                    )
                    isModified = true
                }
                if (excelWriterContext.isHighlightColumnHeadCells) {
                    if (headRow != null && !highlightedColumnHeads.contains(columnDef)) {
                        highlightedColumnHeads.add(columnDef) // Don't highlight column heads twice.
                        // Cell validation error. Highlight column head cell.
                        val headCell = headRow!!.getCell(columnDef._columnNumber)
                        excelWriterContext.cellHighlighter.highlightColumnHeadCell(
                            headCell, excelWriterContext, this,
                            columnDef, headRow
                        )
                        isModified = true
                    }
                }
                if (excelWriterContext.isAddCellComments) { // Cell validation error. Add error message as comment.
                    excelWriterContext.cellHighlighter.setCellComment(cell, validationError.getMessage(i18n))
                    isModified = true
                }
            }
        }
        if (isModified) { // adjust column width to fit the content
            poiSheet.autoSizeColumn(columnWithValidationErrorMessages)
        }
        return this
    }

    @Suppress("unused")
    fun setBigDecimalValue(row: Int, columnHeader: String, value: BigDecimal?): Cell {
        return setBigDecimalValue(row, getColumnDef(columnHeader), value)
    }

    fun setBigDecimalValue(row: Int, col: ExcelColumnDef?, value: BigDecimal?): Cell {
        return setBigDecimalValue(row, col!!._columnNumber, value)
    }

    fun setBigDecimalValue(row: Int, col: Int, value: BigDecimal?): Cell {
        val cell = getCell(row, col, true)!!
        if (value == null) cell.setBlank() else {
            cell.setCellValue(value.toDouble())
            if (value.scale() == 0) {
                cell.cellStyle = excelWorkbook.ensureCellStyle(ExcelCellStandardFormat.INT)
            } else {
                cell.cellStyle = excelWorkbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT)
            }
        }
        return cell
    }

    @Suppress("unused")
    fun setDoubleValue(row: Int, columnHeader: String, value: Double?): Cell {
        return setDoubleValue(row, getColumnDef(columnHeader), value)
    }

    fun setDoubleValue(row: Int, col: ExcelColumnDef?, value: Double?): Cell {
        return setDoubleValue(row, col!!._columnNumber, value)
    }

    fun setDoubleValue(row: Int, col: Int, value: Double?): Cell {
        val cell = getCell(row, col, true)!!
        if (value == null) cell.setBlank() else {
            cell.setCellValue(value)
            cell.cellStyle = excelWorkbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT)
        }
        return cell
    }

    @Suppress("unused")
    fun setIntValue(row: Int, columnHeader: String, value: Int?): Cell {
        return setIntValue(row, getColumnDef(columnHeader), value)
    }

    fun setIntValue(row: Int, col: ExcelColumnDef?, value: Int?): Cell {
        return setIntValue(row, col!!._columnNumber, value)
    }

    fun setIntValue(row: Int, col: Int, value: Int?): Cell {
        val cell = getCell(row, col, true)!!
        if (value == null) cell.setBlank() else {
            cell.setCellValue(value.toDouble())
            cell.cellStyle = excelWorkbook.ensureCellStyle(ExcelCellStandardFormat.INT)
        }
        return cell
    }

    @Suppress("unused")
    fun setStringValue(row: Int, columnHeader: String, value: String?): Cell {
        return setStringValue(row, getColumnDef(columnHeader), value)
    }

    fun setStringValue(row: Int, col: ExcelColumnDef?, value: String?): Cell {
        return setStringValue(row, col!!._columnNumber, value)
    }

    fun setStringValue(row: Int, col: Int, value: String?): Cell {
        val cell = getCell(row, col, true)!!
        if (value == null) cell.setBlank() else cell.setCellValue(value)
        return cell
    }

    @Suppress("unused")
    fun setDateValue(row: Int, columnHeader: String, value: Date?, dateFormat: String?): Cell {
        return setDateValue(row, getColumnDef(columnHeader), value, dateFormat)
    }

    fun setDateValue(row: Int, col: ExcelColumnDef?, value: Date?, dateFormat: String?): Cell {
        return setDateValue(row, col!!._columnNumber, value, dateFormat)
    }

    fun setDateValue(row: Int, col: Int, value: Date?, dateFormat: String?): Cell {
        val cell = getCell(row, col, true)!!
        if (value == null) cell.setBlank() else {
            cell.setCellValue(value)
            cell.cellStyle = excelWorkbook.ensureDateCellStyle(dateFormat!!)
        }
        return cell
    }

    @Suppress("unused")
    fun setDateValue(row: Int, columnHeader: String, value: LocalDate?, dateFormat: String?): Cell {
        return setDateValue(row, getColumnDef(columnHeader), value, dateFormat)
    }

    fun setDateValue(row: Int, col: ExcelColumnDef?, value: LocalDate?, dateFormat: String?): Cell {
        return setDateValue(row, col!!._columnNumber, value, dateFormat)
    }

    fun setDateValue(row: Int, col: Int, value: LocalDate?, dateFormat: String?): Cell {
        val cell = getCell(row, col, true)!!
        if (value == null) cell.setBlank() else {
            cell.setCellValue(value)
            cell.cellStyle = excelWorkbook.ensureDateCellStyle(dateFormat!!)
        }
        return cell
    }

    @Suppress("unused")
    fun setDateValue(row: Int, columnHeader: String, value: LocalDateTime?, dateFormat: String?): Cell {
        return setDateValue(row, getColumnDef(columnHeader), value, dateFormat)
    }

    fun setDateValue(row: Int, col: ExcelColumnDef?, value: LocalDateTime?, dateFormat: String?): Cell {
        return setDateValue(row, col!!._columnNumber, value, dateFormat)
    }

    fun setDateValue(row: Int, col: Int, value: LocalDateTime?, dateFormat: String?): Cell {
        val cell = getCell(row, col, true)!!
        if (value == null) cell.setBlank() else {
            cell.setCellValue(value)
            cell.cellStyle = excelWorkbook.ensureDateCellStyle(dateFormat!!)
        }
        return cell
    }

    /**
     * @param maxMarkedErrors The number of marked errors should be limited (default is 100). Otherwise for very large Excel sheet with a lot of errors, the
     * system may be collapse.
     * <br></br>
     * If set to -1 an unlimited number of errors will be marked.
     * @return This for chaining.
     */
    @Suppress("unused")
    fun setMaxMarkedErrors(maxMarkedErrors: Int): ExcelSheet {
        this.maxMarkedErrors = maxMarkedErrors
        return this
    }

    private fun addValidationError(message: ExcelValidationErrorMessage) {
        if (validationErrors == null) {
            validationErrors = TreeSet()
        }
        validationErrors!!.add(message)
    }

    fun createValidationErrorMissingColumnNumber(columnNumber: Int): ExcelValidationErrorMessage {
        return ExcelValidationErrorMessage(
            MESSAGE_MISSING_COLUMN_NUMBER, ResultMessageStatus.ERROR,
            CellReference.convertNumToColString(columnNumber)
        )
            .setSheet(this).setRow(if (headRow != null) headRow!!.rowNum else 0)
    }

    fun createValidationErrorMissingColumnByName(columnName: String?): ExcelValidationErrorMessage {
        return ExcelValidationErrorMessage(MESSAGE_MISSING_COLUMN_BY_NAME, ResultMessageStatus.ERROR, columnName)
            .setSheet(this).setRow(if (headRow != null) headRow!!.rowNum else 0)
    }

    /**
     * @return true If all specified cells of the row are empty, otherwise false.
     */
    @Suppress("unused")
    fun cellsEmptyCheck(row: Row, vararg columnHeadNames: String): Boolean {
        return cellsEmptyCheck(row, columnHeadNames.toList())
    }

    @Suppress("unused")
    fun cellsEmptyCheck(row: Row, vararg columnNames: ExcelColumnName): Boolean {
        return cellsEmptyCheck(row, columnNames.map { it.head })
    }

    /**
     * @return true If all specified cells of the row are empty, otherwise false.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun cellsEmptyCheck(row: Row, columnHeadNames: List<String>): Boolean {
        columnHeadNames.forEach {
            if (isCellEmpty(row, it))
                return false
        }
        return true
    }

    /**
     * @param columnHeadNames Columns specified by head names to check of row emptiness.
     * @see [isRowEmpty]
     */
    fun setColumnsForRowEmptyCheck(vararg columnHeadNames: String) {
        this.rowEmptyColumns = columnHeadNames.map {
            getColumnDef(it)?.columnNumber ?: -1
        }.filter { it >= 0 }.toIntArray()
    }

    /**
     * @param columnNames Columns specified by head names to check of row emptiness.
     * @see [isRowEmpty]
     */
    fun setColumnsForRowEmptyCheck(vararg columnNames: ExcelColumnName) {
        this.rowEmptyColumns = columnNames.map { getColumnDef(it)?.columnNumber ?: -1 }.filter { it >= 0 }.toIntArray()
    }

    /**
     * @param columnNames Columns to check of row emptiness.
     * @see [isRowEmpty]
     */
    fun setColumnsForRowEmptyCheck(vararg columnDefs: ExcelColumnDef) {
        this.rowEmptyColumns = columnDefs.map { it.columnNumber }.filter { it >= 0 }.toIntArray()
    }

    /**
     * @param columnNames Columns to check of row emptiness (0-based column indices).
     * @see [isRowEmpty]
     */
    fun setColumnsForRowEmptyCheck(vararg columns: Int) {
        this.rowEmptyColumns = columns
    }

    /**
     * @param row Row to check for emptiness.
     * @return true if all cell of the row are empty, or at least all cells of specified columns by [setColumnsForRowEmptyCheck] if given.
     * At default, all columns of the given row will be checked for emptinexx.
     */
    fun isRowEmpty(row: Row): Boolean {
        val columns = this.rowEmptyColumns
        if (columns == null || columns.isEmpty()) {
            for (col in 0 until row.lastCellNum) { // lastCellNum is +1, so do not include it.
                if (!PoiHelper.isEmpty(row.getCell(col)))
                    return false
            }
            return true
        }
        columns.forEach { col ->
            if (!PoiHelper.isEmpty(row.getCell(col)))
                return false
        }
        return true
    }

    @Suppress("unused")
    fun isCellEmpty(row: Row, columnName: ExcelColumnName): Boolean {
        return isCellEmpty(row, columnName.head)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun isCellEmpty(row: Row, columnHeadname: String): Boolean {
        val cell = getCell(row, columnHeadname)
        return PoiHelper.isEmpty(cell)
    }

    fun getRow(rownum: Int): ExcelRow {
        var excelRow = excelRowMap[rownum]
        if (excelRow == null) {
            var row: Row?
            if (rownum < poiSheet.lastRowNum) {
                row = poiSheet.getRow(rownum)
                if (row == null) {
                    row = poiSheet.createRow(rownum)
                }
            } else {
                while (poiSheet.getRow(rownum).also { row = it } == null) {
                    excelRow = createRow()
                }
            }
            if (excelRow == null) { // Poi row exists, but excel cell not yet:
                excelRow = ensureRow(row)
            }
        }
        return excelRow
    }

    fun cleanSheet() {
        val numberOfRows = poiSheet.lastRowNum
        if (numberOfRows >= 0) {
            return  // Nothing to do.
        }
        isModified = true
        for (i in numberOfRows downTo 0) {
            if (poiSheet.getRow(i) != null) {
                poiSheet.removeRow(poiSheet.getRow(i))
            }
        }
    }

    /**
     * Appends the row.
     *
     * @return The created row.
     */
    fun createRow(): ExcelRow {
        var rowCount = poiSheet.lastRowNum
        if (rowCount == 0 && poiSheet.getRow(0) == null) {
            rowCount = -1
        }
        return createRow(rowCount + 1)
    }

    /**
     * Appends the row.
     *
     * @return The created row.
     */
    private fun createRow(rowNum: Int): ExcelRow {
        val row = poiSheet.createRow(rowNum)
        return ensureRow(row)
    }

    /**
     * Shifts the rows..
     * @param startRow First row to shift.
     * @param endRow Last row to shift (last row of sheet as default).
     * @param n Number of rows to shift (default is 1).
     */
    @JvmOverloads
    fun shiftRows(startRow: Int, endRow: Int? = null, n: Int = 1) {
        poiSheet.shiftRows(startRow, endRow ?: poiSheet.lastRowNum, n)
        clearRowMap()
    }

    /**
     * Should be called after shifting or inserting rows.
     */
    fun clearRowMap() {
        excelRowMap.clear()
    }

    private fun ensureRow(row: Row?): ExcelRow {
        var excelRow = excelRowMap[row!!.rowNum]
        if (excelRow == null) {
            excelRow = ExcelRow(this, row)
            excelRowMap[row.rowNum] = excelRow
        }
        return excelRow
    }

    fun autosize() {
        for (i in 0..lastColumn) {
            poiSheet.autoSizeColumn(i)
        }
    }

    @Suppress("unused")
    fun autosize(columnIndex: Int) {
        poiSheet.autoSizeColumn(columnIndex)
    }

    fun setColumnWidth(column: ExcelColumnDef, width: Int) {
        poiSheet.setColumnWidth(column.columnNumber, width)
    }

    fun setColumnWidth(columnIndex: Int, width: Int) {
        poiSheet.setColumnWidth(columnIndex, width)
    }

    /**
     * @param column  Column as enum. The ordinal value will be used as column number.
     */
    @Suppress("unused")
    fun setColumnWidth(column: Enum<*>, width: Int) {
        setColumnWidth(column.ordinal, width)
    }

    @Suppress("unused")
    fun addMergeRegion(range: CellRangeAddress?) {
        poiSheet.addMergedRegion(range)
    }

    /**
     * Merges cells and sets the value.
     *
     * @param firstRow
     * @param lastRow
     * @param firstCol
     * @param lastCol
     * @param value
     */
    @Suppress("unused")
    fun setMergedRegion(firstRow: Int, lastRow: Int, firstCol: Int, lastCol: Int, value: Any): ExcelCell {
        val region = CellRangeAddress(firstRow, lastRow, firstCol, lastCol)
        poiSheet.addMergedRegion(region)
        val row = getRow(firstRow)
        return row.getCell(firstCol).setCellValue(value)
    }

    /**
     * Freezes the first toCol columns and the first toRow lines.
     *
     * @param toCol
     * @param toRow
     * @see Sheet.createFreezePane
     */
    @Suppress("unused")
    fun createFreezePane(toCol: Int, toRow: Int) {
        poiSheet.createFreezePane(toCol, toRow)
    }

    /**
     * @param scale
     * @see Sheet.setZoom
     */
    @Suppress("unused")
    fun setZoom(scale: Int) {
        poiSheet.setZoom(scale)
    }

    /**
     * Set auto-filter for the whole first row. Must be called after adding the first row with all heading cells.
     */
    @Suppress("unused")
    fun setAutoFilter() {
        val headingRow = if (headRow != null) headRow!!.rowNum else 0
        val lastCol = getRow(headingRow).lastCellNum.toInt()
        val range = CellRangeAddress(headingRow, headingRow, 0, lastCol - 1)
        poiSheet.setAutoFilter(range)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val lastColumn: Int
        get() {
            var lastCol = 0
            for (row in poiSheet) {
                if (row.lastCellNum > lastCol) {
                    lastCol = row.lastCellNum.toInt()
                }
            }
            return lastCol
        }

    companion object {
        const val MESSAGE_MISSING_COLUMN_NUMBER = "merlin.excel.validation_error.missing_column_number"
        const val MESSAGE_MISSING_COLUMN_BY_NAME = "merlin.excel.validation_error.missing_column_by_name"
        private val log = LoggerFactory.getLogger(ExcelSheet::class.java)
    }
}
