package de.micromata.merlin.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.DateUtil
import org.slf4j.LoggerFactory

/**
 * Some helper classes.
 */
object PoiHelper {
    private val log = LoggerFactory.getLogger(PoiHelper::class.java)
    /**
     * @param trimValue If true, the result string will be trimmed. Default is false.
     */
    @JvmStatic
    @JvmOverloads
    fun getValueAsString(cell: Cell?, trimValue: Boolean = false): String? {
        if (cell == null) {
            return null
        }
        val result =
                if (cell.cellType == CellType.STRING) {
                    cell.stringCellValue
                } else {
                    val formatter = DataFormatter()
                    formatter.formatCellValue(cell)
                }
        return if (trimValue) result?.trim { it <= ' ' }
        else result
    }

    /**
     * @param localDateTime If true, any dates will be returned as [LocalDateTime], otherwise as [java.util.Date]
     */
    @JvmStatic
    @JvmOverloads
    fun getValue(cell: Cell?, localDateTime: Boolean = true): Any? {
        return if (cell == null) {
            null
        } else when (cell.cellType) {
            CellType.BOOLEAN -> cell.booleanCellValue
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    if (localDateTime)
                        cell.localDateTimeCellValue
                    else
                        cell.dateCellValue
                } else cell.numericCellValue
            }
            CellType.STRING -> cell.stringCellValue
            CellType.BLANK -> null
            else -> {
                log.warn("Unsupported Excel cell type: " + cell.cellType)
                getValueAsString(cell)
            }
        }
    }

    @JvmStatic
    fun isEmpty(cell: Cell?): Boolean {
        if (cell == null) {
            return true
        }
        if (cell.cellType == CellType.BLANK) {
            return true
        }
        return cell.cellType == CellType.STRING && cell.stringCellValue.trim { it <= ' ' }.isEmpty()
    }

    @JvmStatic
    @JvmOverloads
    fun setComment(cell: ExcelCell, message: String?, author: String? = null) {
        setComment(cell.cell, message, author)
    }

    @JvmStatic
    @JvmOverloads
    fun setComment(cell: Cell, message: String?, author: String? = null) {
        val actComment = cell.cellComment
        if (actComment != null) {
            log.error("Cell comment does already exist. Can't add cell comment twice.")
            return
        }
        val drawing = cell.sheet.createDrawingPatriarch()
        val factory = cell.sheet.workbook.creationHelper
        // When the comment box is visible, have it show in a 1x3 space
        val anchor = factory.createClientAnchor()
        anchor.setCol1(cell.columnIndex)
        anchor.setCol2(cell.columnIndex + 3)
        anchor.row1 = cell.rowIndex
        anchor.row2 = cell.rowIndex + 3
        // Create the comment and set the text+author
        val comment = drawing.createCellComment(anchor)
        val str = factory.createRichTextString(message)
        comment.string = str
        if (!author.isNullOrBlank())
            comment.setAuthor(author);
        // Assign the comment to the cell
        cell.cellComment = comment
    }
}
