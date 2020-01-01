package de.micromata.merlin.excel

import de.micromata.merlin.I18n
import de.micromata.merlin.ResultMessage
import de.micromata.merlin.ResultMessageStatus
import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

class ExcelValidationErrorMessage(messageId: String,
                                  status: ResultMessageStatus,
                                  vararg parameters: Any?)
    : ResultMessage(messageId, status, *parameters), Comparable<Any> {
    var row = 0
        private set
    var columnDef: ExcelColumnDef? = null
        private set
    var cellValue: Any? = null
        private set

    /**
     * @return Previous set sheet or sheet of columnDef if available.
     */
    var sheet: ExcelSheet? = null
        get() = if (field != null) field else columnDef?.sheet
        private set

    val sheetName: String
        get() = sheet?.sheetName ?: ""

    /**
     * @param i18n The i18n implementation to use for translation.
     * @return Message including sheet name, column and row.
     */
    fun getMessageWithAllDetails(i18n: I18n): String { // 0 - sheet name, 1 - column number as letters, 2 - column name, 3 - row number, 4 - message.
        return i18n.formatMessage("merlin.excel.validation_error.display_all",
                sheetName,
                if (columnDef != null) columnDef!!.columnNumberAsLetters else "",
                if (columnDef != null) columnDef!!.columnHeadname else "",
                row + 1,
                getMessage(i18n))
    }

    /**
     * @param i18n The i18n implementation to use for translation.
     * @return Message including sheet name, column and row.
     */
    fun getMessageWithSheetName(i18n: I18n): String { // 0 - sheet name, 1 - message.
        return i18n.formatMessage("merlin.excel.validation_error.display_sheet",
                sheetName,
                getMessage(i18n))
    }

    /**
     * @param i18n The i18n implementation to use for translation.
     * @return Message including column number/name.
     */
    fun getMessageWithColumn(i18n: I18n): String { // 0 - sheet name, 1 - message.
        return if (columnDef != null) {
            i18n.formatMessage("merlin.excel.validation_error.display_column",
                    columnDef!!.columnNumberAsLetters,
                    columnDef!!.columnHeadname,
                    getMessage(i18n))
        } else { // Column not given. So can't display column.
            getMessage(i18n)
        }
    }

    override fun getMessage(i18n: I18n): String {
        val params = mutableListOf<Any?>()
        if (cellValue != null)
            params.add(cellValue)
        if (parameters.isNotEmpty()) {
            params.addAll(parameters)
        }
        return if (params.isNullOrEmpty()) {
            i18n.getMessage(messageId)
        } else {
            i18n.formatMessage(messageId, *params.toTypedArray())
        }
    }


    fun setRow(row: Int): ExcelValidationErrorMessage {
        this.row = row
        return this
    }

    fun setColumnDef(columnDef: ExcelColumnDef?): ExcelValidationErrorMessage {
        this.columnDef = columnDef
        validateSheets()
        return this
    }

    fun setSheet(sheet: ExcelSheet?): ExcelValidationErrorMessage {
        this.sheet = sheet
        validateSheets()
        return this
    }

    private fun validateSheets() {
        val columnDefSheet = columnDef?.sheet
        if (sheet == null || columnDefSheet == null)
            return
        if (columnDefSheet != sheet) {
            throw IllegalStateException("Sheet '${sheet?.sheetName} differs from sheet assigned to columnDef '${columnDefSheet.sheetName}.")
        }
    }

    /**
     * @param cellValue The value to set.
     * @return this for chaining.
     */
    fun setCellValue(cellValue: Any?): ExcelValidationErrorMessage {
        this.cellValue = cellValue
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ExcelValidationErrorMessage) {
            return false
        }
        val builder = EqualsBuilder()
        builder.append(row, other.row)
        builder.append(messageId, other.messageId)
        builder.append(columnDef, other.columnDef)
        return builder.isEquals
    }

    override fun hashCode(): Int {
        val builder = HashCodeBuilder()
        builder.append(row)
        builder.append(messageId)
        builder.append(columnDef)
        return builder.hashCode()
    }

    override fun compareTo(other: Any): Int {
        if (this == other) {
            return 0
        }
        other as ExcelValidationErrorMessage
        val sheetIndex1 = sheet?.sheetIndex ?: -1
        val sheetIndex2 = other.sheet?.sheetIndex ?: -1
        val columnNumber1 = columnDef?.columnNumber ?: -1
        val columnNumber2 = other.columnDef?.columnNumber ?: -1
        return CompareToBuilder()
                .append(sheetIndex1, sheetIndex2)
                .append(row, other.row)
                .append(columnNumber1, columnNumber2)
                .append(messageId, other.messageId)
                .toComparison()
    }
}
