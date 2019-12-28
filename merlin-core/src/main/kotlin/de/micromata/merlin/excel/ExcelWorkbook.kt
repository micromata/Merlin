package de.micromata.merlin.excel

import de.micromata.merlin.CoreI18n
import de.micromata.merlin.persistency.PersistencyRegistry
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.poi.ss.usermodel.*
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import java.util.*

/**
 * Wraps and enhances a POI workbook.
 */
class ExcelWorkbook : AutoCloseable {
    lateinit var pOIWorkbook: Workbook
        private set
    private val sheetList = mutableListOf<ExcelSheet>()
    private var sheetListInitialized = false
    private val cellStyleMap: MutableMap<String, CellStyle?> = HashMap()
    private val fontMap: MutableMap<String, Font?> = HashMap()
    private var inputStream: InputStream? = null
    var formulaEvaluator: FormulaEvaluator? = null
        get() {
            if (field == null) {
                field = pOIWorkbook.creationHelper.createFormulaEvaluator()
            }
            return field
        }
        private set

    constructor(workbook: Workbook) {
        pOIWorkbook = workbook
    }

    constructor(excelFilename: String) : this(File(excelFilename)) {}
    constructor(excelFile: File) {
        try {
            val fis = FileInputStream(excelFile)
            open(fis, excelFile.name)
        } catch (ex: IOException) {
            log.error("Couldn't open File '" + excelFile.absolutePath + "': " + ex.message)
            throw RuntimeException(ex)
        }
    }

    /**
     * @param inputStream The input stream to read the Excel content from.
     * @param filename    Only for logging purposes if any error occurs.
     */
    constructor(inputStream: InputStream, filename: String) {
        open(inputStream, filename)
    }

    private fun open(inputStream: InputStream, filename: String) {
        this.inputStream = inputStream
        try {
            pOIWorkbook = WorkbookFactory.create(inputStream)
        } catch (ex: IOException) {
            log.error("Couldn't open File '" + filename + "' from InputStream: " + ex.message, ex)
            throw RuntimeException(ex)
        }
    }

    fun getSheet(idx: Int): ExcelSheet? {
        initializeSheetList()
        return sheetList[idx]
    }

    fun getSheet(sheetName: String?): ExcelSheet? {
        initializeSheetList()
        if (sheetName == null) {
            log.error("Can't get sheet by name without given name. Name parameter is null.")
            return null
        }
        for (sheet in sheetList) {
            if (sheetName == sheet.sheetName) {
                return sheet
            }
        }
        return null
    }

    /**
     * A sheet name might be localized, such as "Configuration" and "Konfiguration". Try to get the first sheet matching one
     * of the given sheetNames.
     *
     * @param i18nKey of the sheet title
     * @return The specified Excel sheet or null if not found.
     */
    fun getSheetByLocalizedNames(i18nKey: String?): ExcelSheet? {
        return getSheetByLocalizedNames(CoreI18n.getAllTranslations(i18nKey))
    }

    /**
     * A sheet name might be localized, such as "Configuration" and "Konfiguration". Try to get the first sheet matching one
     * of the given sheetNames.
     *
     * @param sheetNames The sheet names to look for.
     * @return The specified Excel sheet or null if not found.
     */
    fun getSheetByLocalizedNames(sheetNames: Set<String?>): ExcelSheet? {
        if (CollectionUtils.isEmpty(sheetNames)) {
            return null
        }
        var sheet: ExcelSheet?
        for (sheetName in sheetNames) {
            sheet = getSheet(sheetName)
            if (sheet != null) {
                return sheet
            }
        }
        return null
    }

    fun createOrGetSheet(sheetName: String?): ExcelSheet? {
        initializeSheetList()
        if (sheetName == null) {
            log.error("Can't get sheet by name without given name. Name parameter is null.")
            return null
        }
        var sheet = getSheet(sheetName)
        if (sheet != null) {
            return sheet
        }
        sheet = ExcelSheet(this, pOIWorkbook.createSheet(sheetName))
        sheet.isModified = true
        synchronized(this) {
            sheetListInitialized = false
            sheetList.clear()
        }
        return sheet
    }

    private fun initializeSheetList() {
        synchronized(this) {
            if (sheetListInitialized) {
                return  // Already initialized.
            }
            for (poiSheet in pOIWorkbook) {
                val excelSheet = ExcelSheet(this, poiSheet)
                sheetList.add(excelSheet)
            }
            sheetListInitialized = true
        }
    }

    /**
     * @return true if any sheet of this workbook returns true: [ExcelSheet.isModified]
     */
    val isModified: Boolean
        get() {
            initializeSheetList()
            for (sheet in sheetList) {
                if (sheet.isModified) {
                    return true
                }
            }
            return false
        }

    fun doesCellStyleExist(id: String?): Boolean {
        return cellStyleMap.containsKey(id)
    }

    /**
     * Please re-use cell styles due to limitations of Excel.
     *
     * @param id Id of the cell style.
     * @return The CellStyle to use.
     */
    fun createOrGetCellStyle(id: String): CellStyle? {
        var cellStyle = cellStyleMap[id]
        if (cellStyle == null) {
            cellStyle = pOIWorkbook.createCellStyle()
            cellStyleMap[id] = cellStyle
        }
        return cellStyle
    }

    fun ensureCellStyle(format: ExcelCellStandardFormat): CellStyle? {
        val exist = doesCellStyleExist("DataFormat." + format.name)
        val cellStyle = createOrGetCellStyle("DataFormat." + format.name)
        if (!exist) {
            if (format == ExcelCellStandardFormat.FLOAT) {
                cellStyle!!.dataFormat = getDataFormat("#.#")
            } else if (format == ExcelCellStandardFormat.INT) {
                cellStyle!!.dataFormat = BuiltinFormats.getBuiltinFormat("0").toShort()
            } else require(format != ExcelCellStandardFormat.DATE) { "Please call ensureDateCellStyle instead of ensureCellStyle." }
        }
        return cellStyle
    }

    fun ensureDateCellStyle(dateFormat: String): CellStyle? {
        val exist = doesCellStyleExist("DataFormat." + ExcelCellStandardFormat.DATE.name + "." + dateFormat)
        val cellStyle = createOrGetCellStyle("DataFormat." + ExcelCellStandardFormat.DATE.name + "." + dateFormat)
        if (!exist) {
            cellStyle!!.dataFormat = getDataFormat(dateFormat)
        }
        return cellStyle
    }

    fun createDataFormat(): DataFormat {
        return pOIWorkbook.creationHelper.createDataFormat()
    }

    /**
     * Gets or creates a new data format.
     * @param format The cell format (Excel style).
     * @return The id of the created or reused data format.
     */
    fun getDataFormat(format: String?): Short {
        return createDataFormat().getFormat(format)
    }

    /**
     * Please re-use cell styles due to limitations of Excel.
     *
     * @param id The font id to re-use or create.
     * @return The font to use.
     */
    fun createOrGetFont(id: String): Font? {
        var font = fontMap[id]
        if (font == null) {
            font = pOIWorkbook.createFont()
            fontMap[id] = font
        }
        return font
    }

    val asByteArrayOutputStream: ByteArrayOutputStream
        get() {
            val bos = ByteArrayOutputStream()
            try {
                pOIWorkbook.write(bos)
            } catch (ex: IOException) {
                log.error(ex.message, ex)
                throw RuntimeException(ex)
            }
            return bos
        }

    val numberOfSheets
        get() = pOIWorkbook.numberOfSheets

    override fun close() {
        try {
            if (inputStream != null) {
                inputStream!!.close()
            }
            pOIWorkbook.close()
        } catch (ioe: IOException) { // ignore
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ExcelWorkbook::class.java)
        @JvmStatic
        fun create(path: Path): ExcelWorkbook? {
            val inputStream = PersistencyRegistry.getDefault().getInputStream(path)
            if (inputStream == null) {
                log.error("Cam't get input stream for path: " + path.toAbsolutePath())
                return null
            }
            val filename = path.fileName.toString()
            return ExcelWorkbook(inputStream, filename)
        }
    }
}
