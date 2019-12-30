package de.micromata.merlin.excel

import de.micromata.merlin.CoreI18n
import de.micromata.merlin.I18n
import de.micromata.merlin.ResultMessageStatus
import de.micromata.merlin.data.Data
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.CellReference
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.*

/**
 * Wraps and enhances a POI sheet.
 */
class ExcelSheet internal constructor(workbook: ExcelWorkbook, poiSheet: Sheet) {
    private val log = LoggerFactory.getLogger(ExcelSheet::class.java)
    private val columnDefList: MutableList<ExcelColumnDef> = ArrayList()
    val poiSheet: Sheet
    val excelWorkbook: ExcelWorkbook
    /**
     * If true, all cell values will be trimmed before getting the cell value as string. The Excel cell itself will
     * not be modified. Default is false.
     * See: [getCellString]
     */
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
     * @return true, if this sheet was modified (by calling [.markErrors].
     */
    var isModified = false
    private var i18n: I18n
    private var maxMarkedErrors = 100
    private val excelRowMap: MutableMap<Int, ExcelRow> = HashMap()
    fun setI18n(i18n: I18n) {
        this.i18n = i18n
    }

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
     * ExcelColumnListener. If no Analyzer is set for a columng, the column cells will not be analyzed.
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
    fun registerColumns(vararg columnHeads: String): ExcelSheet {
        for (columnHead in columnHeads) {
            if (_getColumnDef(columnHead) != null) {
                log.error("Don't register column heads twice: '$columnHead'.")
                continue
            }
            columnDefList.add(ExcelColumnDef(columnHead))
        }
        return this
    }

    /**
     * @param columnHead The column head to register.
     * @param aliases Sometimes table heads changed for different imports. Register any used aliases for detecting the column by aliases too.
     * @return Created and registered ExcelColumnDef.
     */
    fun registerColumn(columnHead: String, vararg aliases: String): ExcelColumnDef {
        val columnDef = ExcelColumnDef(columnHead, *aliases)
        val existing = _getColumnDef(columnHead)
        if (existing != null) {
            columnDef.occurrenceNumber = existing.occurrenceNumber + 1
            log.info("Multiple registration of column head '$columnHead': #${columnDef.occurrenceNumber}")
        }
        columnDefList.add(columnDef)
        return columnDef
    }

    /**
     * @param columnHead The column head to register.
     * @param listener   The listener to use.
     * @return Created and registered ExcelColumnDef.
     */
    fun registerColumn(columnHead: String, listener: ExcelColumnListener): ExcelColumnDef {
        var columnDef = _getColumnDef(columnHead)
        if (columnDef == null) {
            columnDef = ExcelColumnDef(columnHead)
            columnDefList.add(columnDef)
        }
        registerColumn(columnDef, listener)
        return columnDef
    }

    /**
     * @param columnDef The column to register.
     * @param listener  The listener to use.
     * @return this for chaining.
     */
    fun registerColumn(columnDef: ExcelColumnDef, listener: ExcelColumnListener): ExcelSheet {
        columnDef.addColumnListener(listener)
        listener.setSheet(this)
        return this
    }

    /**
     * @return Iterator for rows. Iterator starts with data row (head row + 1).
     */
    val dataRowIterator: Iterator<Row>
        get() {
            findAndReadHeadRow()
            val it = poiSheet.rowIterator()
            if (headRow == null)
                return it
            while (it.hasNext()) {
                if (it.next() == headRow!!.row) {
                    break
                }
            }
            return it
        }

    fun readRow(row: Row, data: Data) {
        findAndReadHeadRow()
        for (columnDef in columnDefList) {
            val cell = row.getCell(columnDef._columnNumber)
            val value = PoiHelper.getValueAsString(cell)
            data.put(columnDef.columnHeadname, value)
        }
    }

    /**
     * @param row            The row to get the cell value from.
     * @param columnHeadname The name of the column to get.
     * @param nullAsEmpty    If true, null cell
     * @param trimValue      If true, the returned value will be trimmed, default is [autotrimCellValues].
     * @return The String value of the specified column cell.
     */
    @JvmOverloads
    fun getCellString(row: Row, columnHeadname: String, nullAsEmpty: Boolean = true, trimValue: Boolean = autotrimCellValues): String? {
        // findAndReadHeadRow(); Will be called in getColumnDef
        val cell = getCell(row, columnHeadname) ?: return if (nullAsEmpty) "" else null
        return PoiHelper.getValueAsString(cell, trimValue)
    }

    /**
     * @param row      The row to get the cell value from.
     * @param columnDef The column to get.
     * @param nullAsEmpty    If true, null cell
     * @param trimVal        If true, the returned value will be trimmed, default is [autotrimCellValues].
     * @return The String value of the specified column cell.
     */
    fun getCellString(row: Row, columnDef: ExcelColumnDef?, nullAsEmpty: Boolean = true, trimValue: Boolean = autotrimCellValues): String? {
        // findAndReadHeadRow(); Will be called in getColumnDef
        val cell = getCell(row, columnDef) ?: return if (nullAsEmpty) "" else null
        return PoiHelper.getValueAsString(cell, trimValue)
    }

    /**
     * @param row            The row to get the cell value from.
     * @param columnHeadname The name of the column to get.
     * @return The String value of the specified column cell.
     */
    fun getCellInt(row: Row, columnHeadname: String): Int? { // findAndReadHeadRow(); Will be called in getColumnDef
        val cell = getCell(row, columnHeadname) ?: return null
        if (cell.getCellType() != CellType.NUMERIC) {
            log.warn("Cell of column '$columnHeadname' in row ${row.rowNum} of sheet '$sheetName' isn't of type numeric: '${cell}'.")
            return null
        }
        return cell.numericCellValue.toInt()
    }

    /**
     * @param row            The row to get the cell value from.
     * @param columnHeadname The name of the column to get.
     * @return The String value of the specified column cell.
     */
    fun getCellDouble(row: Row, columnHeadname: String): Double? { // findAndReadHeadRow(); Will be called in getColumnDef
        val cell = getCell(row, columnHeadname) ?: return null
        if (cell.getCellType() != CellType.NUMERIC) {
            log.warn("Cell of column '$columnHeadname' in row ${row.rowNum} of sheet '$sheetName' isn't of type numeric: '${cell}'.")
            return null
        }
        return cell.numericCellValue
    }

    /**
     * @param row            The row to get the cell value from.
     * @param columnHeadname The name of the column to get.
     * @return The String value of the specified column cell.
     */
    fun getCellDate(row: Row, columnHeadname: String): Date? { // findAndReadHeadRow(); Will be called in getColumnDef
        val cell = getCell(row, columnHeadname) ?: return null
        if (cell.getCellType() != CellType.NUMERIC) {
            log.warn("Cell of column '$columnHeadname' in row ${row.rowNum} of sheet '$sheetName' isn't of type numeric: '${cell}'.")
            return null
        }
        return cell.dateCellValue
    }

    /**
     * @param row            The row to get the cell from.
     * @param columnHeadname The name of the column to get the cell from.
     * @return The cell of the specified column of the current row (uses internal interator).
     */
    fun getCell(row: Row, columnHeadname: String): Cell? { // findAndReadHeadRow(); Will be called in getColumnDef
        val columnDef = getColumnDef(columnHeadname)
        return getCell(row, columnDef)
    }

    /**
     * @param row       The row to get the cell from.
     * @param columnDef The specified column to get the cell from.
     * @return The cell of the specified column of the current row (uses internal interator).
     */
    fun getCell(row: Row, columnDef: ExcelColumnDef?): Cell? {
        findAndReadHeadRow()
        if (columnDef == null) {
            return null
        }
        if (columnDef._columnNumber < 0) {
            log.debug("Column '" + columnDef.columnHeadname + "' not found in sheet '" + sheetName + "': can't run cell.")
            return null
        }
        return row.getCell(columnDef._columnNumber)
    }

    /**
     * @param row       Excel row number (starting with 0, POI row number).
     * @param columnDef The specified column to get the cell from.
     * @return The specified cell.
     */
    fun getCell(row: Int, columnDef: ExcelColumnDef): Cell {
        findAndReadHeadRow()
        return getCell(row, columnDef._columnNumber)
    }

    /**
     * @param rowNum       Excel row number (starting with 0, POI row number).
     * @param columnNumber The specified column to get the cell from.
     * @return The specified cell.
     */
    fun getCell(rowNum: Int, columnNumber: Int): Cell {
        findAndReadHeadRow()
        return getRow(rowNum)!!.getCell(columnNumber).cell
    }

    private fun findAndReadHeadRow() {
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
        var col = -1
        val occurrenceMap = mutableMapOf<String, Int>()
        for (cell in current) {
            ++col
            val strVal = PoiHelper.getValueAsString(cell)
            if (strVal.isNullOrBlank())
                continue
            log.debug("Reading head column '$strVal' in column $col")
            val normalizedHeaderName = ExcelColumnDef.normalizedHeaderName(strVal)
            val occurrenceNumber = occurrenceMap[normalizedHeaderName] ?: 1
            occurrenceMap[normalizedHeaderName] = occurrenceNumber + 1
            val columnDef = _getColumnDef(strVal, occurrenceNumber)
            if (columnDef != null) {
                log.debug("Head column found: '$strVal' in col #$col")
                columnDef._columnNumber = col
            } else {
                log.debug("Head column not registered: '$strVal'.")
            }
        }
    }

    fun getColumnDef(columnHeadname: String): ExcelColumnDef? {
        findAndReadHeadRow()
        return _getColumnDef(columnHeadname)
    }

    fun _getColumnDef(columnHeadname: String, occurrenceNumber: Int = 1): ExcelColumnDef? {
        if (StringUtils.isEmpty(columnHeadname)) {
            return null
        }
        for (columnDef in columnDefList) {
            if (columnDef.match(columnHeadname)) {
                if (occurrenceNumber == columnDef.occurrenceNumber) {
                    log.debug("Column '$columnHeadname' found.")
                    return columnDef
                } else {
                    log.debug("Skipping of '$columnHeadname' in col #${columnDef._columnNumber}. Looking further for occurence #$occurrenceNumber")
                }
            }
        }
        log.debug("Column definition '$columnHeadname' not found.")
        return null
    }

    fun getColumnDef(columnNumber: Int): ExcelColumnDef? {
        for (columnDef in columnDefList) {
            if (columnNumber == columnDef._columnNumber) {
                return columnDef
            }
        }
        return null
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
            val allValidationErrors: MutableSet<ExcelValidationErrorMessage> = TreeSet()
            if (validationErrors != null) {
                allValidationErrors.addAll(validationErrors!!)
            }
            for (columnDef in columnDefList) {
                if (!columnDef.hasColumnListeners()) {
                    continue
                }
                for (columnListener in columnDef.getColumnListeners()!!) {
                    if (columnListener !is ExcelColumnValidator) {
                        continue
                    }
                    val columnValidator = columnListener
                    if (columnValidator.hasValidationErrors()) {
                        allValidationErrors.addAll(columnValidator.validationErrors)
                    }
                }
            }
            return allValidationErrors
        }

    /**
     * Marks and comments validation errors of cells of this sheet by manipulating the Excel sheet.
     * Refer [.isModified] for checking if any modification was done.
     * Please don't forget to call [.analyze] first with parameter validate=true.
     *
     * @param i18N               For localizing messages.
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
                excelWriterContext.errorMessageWriter.updateOrCreateCell(excelWriterContext, this,
                        columnWithValidationErrorMessages, row, validationError)
                isModified = true
            }
            if (columnDef != null) {
                var cell = row!!.getCell(columnDef._columnNumber)
                if (cell == null) {
                    cell = row.getCell(columnDef._columnNumber, ExcelCellType.STRING)
                }
                if (excelWriterContext.isHighlightErrorCells) { // Cell validation error. Highlight cell.
                    excelWriterContext.cellHighlighter.highlightErrorCell(cell, excelWriterContext, this,
                            columnDef, row)
                    isModified = true
                }
                if (excelWriterContext.isHighlightColumnHeadCells) {
                    if (headRow != null && !highlightedColumnHeads.contains(columnDef)) {
                        highlightedColumnHeads.add(columnDef) // Don't highlight column heads twice.
                        // Cell validation error. Highlight column head cell.
                        val headCell = headRow!!.getCell(columnDef._columnNumber)
                        excelWriterContext.cellHighlighter.highlightColumnHeadCell(headCell, excelWriterContext, this,
                                columnDef, headRow)
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

    fun setBigDecimalValue(row: Int, columnHeader: String, value: BigDecimal?): Cell {
        return setBigDecimalValue(row, getColumnDef(columnHeader), value)
    }

    fun setBigDecimalValue(row: Int, col: ExcelColumnDef?, value: BigDecimal?): Cell {
        return setBigDecimalValue(row, col!!._columnNumber, value)
    }

    fun setBigDecimalValue(row: Int, col: Int, value: BigDecimal?): Cell {
        val cell = getCell(row, col)
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

    fun setDoubleValue(row: Int, columnHeader: String, value: Double?): Cell {
        return setDoubleValue(row, getColumnDef(columnHeader), value)
    }

    fun setDoubleValue(row: Int, col: ExcelColumnDef?, value: Double?): Cell {
        return setDoubleValue(row, col!!._columnNumber, value)
    }

    fun setDoubleValue(row: Int, col: Int, value: Double?): Cell {
        val cell = getCell(row, col)
        if (value == null) cell.setBlank() else {
            cell.setCellValue(value)
            cell.cellStyle = excelWorkbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT)
        }
        return cell
    }

    fun setIntValue(row: Int, columnHeader: String, value: Int?): Cell {
        return setIntValue(row, getColumnDef(columnHeader), value)
    }

    fun setIntValue(row: Int, col: ExcelColumnDef?, value: Int?): Cell {
        return setIntValue(row, col!!._columnNumber, value)
    }

    fun setIntValue(row: Int, col: Int, value: Int?): Cell {
        val cell = getCell(row, col)
        if (value == null) cell.setBlank() else {
            cell.setCellValue(value.toDouble())
            cell.cellStyle = excelWorkbook.ensureCellStyle(ExcelCellStandardFormat.INT)
        }
        return cell
    }

    fun setStringValue(row: Int, columnHeader: String, value: String?): Cell {
        return setStringValue(row, getColumnDef(columnHeader), value)
    }

    fun setStringValue(row: Int, col: ExcelColumnDef?, value: String?): Cell {
        return setStringValue(row, col!!._columnNumber, value)
    }

    fun setStringValue(row: Int, col: Int, value: String?): Cell {
        val cell = getCell(row, col)
        if (value == null) cell.setBlank() else cell.setCellValue(value)
        return cell
    }

    fun setDateValue(row: Int, columnHeader: String, value: Date?, dateFormat: String?): Cell {
        return setDateValue(row, getColumnDef(columnHeader), value, dateFormat)
    }

    fun setDateValue(row: Int, col: ExcelColumnDef?, value: Date?, dateFormat: String?): Cell {
        return setDateValue(row, col!!._columnNumber, value, dateFormat)
    }

    fun setDateValue(row: Int, col: Int, value: Date?, dateFormat: String?): Cell {
        val cell = getCell(row, col)
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
        return ExcelValidationErrorMessage(MESSAGE_MISSING_COLUMN_NUMBER, ResultMessageStatus.ERROR,
                CellReference.convertNumToColString(columnNumber))
                .setSheet(this).setRow(if (headRow != null) headRow!!.rowNum else 0)
    }

    fun createValidationErrorMissingColumnByName(columnName: String?): ExcelValidationErrorMessage {
        return ExcelValidationErrorMessage(MESSAGE_MISSING_COLUMN_BY_NAME, ResultMessageStatus.ERROR, columnName)
                .setSheet(this).setRow(if (headRow != null) headRow!!.rowNum else 0)
    }

    fun getRow(rownum: Int): ExcelRow? {
        var excelRow = excelRowMap[rownum]
        if (excelRow == null) {
            var row: Row?
            while (poiSheet.getRow(rownum).also { row = it } == null) {
                excelRow = createRow()
            }
            if (excelRow == null) { // Poi cell exists, but excel cell not yet:
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
        val row = poiSheet.createRow(rowCount + 1)
        return ensureRow(row)
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

    fun autosize(columnIndex: Int) {
        poiSheet.autoSizeColumn(columnIndex)
    }

    fun setColumnWidth(columnIndex: Int, width: Int) {
        poiSheet.setColumnWidth(columnIndex, width)
    }

    fun addMergeRegion(range: CellRangeAddress?) {
        poiSheet.addMergedRegion(range)
    }

    /**
     * Set auto-filter for the whole first row. Must be called after adding the first row with all heading cells.
     */
    fun setAutoFilter() {
        val headingRow = if (headRow != null) headRow!!.rowNum else 0
        val lastCol = getRow(headingRow)!!.lastCellNum.toInt()
        val range = CellRangeAddress(headingRow, headingRow, 0, lastCol - 1)
        poiSheet.setAutoFilter(range)
    }

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
    }

    init {
        log.debug("Reading sheet '" + poiSheet.sheetName + "'")
        excelWorkbook = workbook
        this.poiSheet = poiSheet
        i18n = CoreI18n.getDefault()
    }
}