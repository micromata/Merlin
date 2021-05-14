package de.micromata.merlin.excel

import de.micromata.merlin.CoreI18n
import de.micromata.merlin.persistency.PersistencyRegistry
import mu.KotlinLogging
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellReference
import java.io.*
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

private val log = KotlinLogging.logger {}

/**
 * Wraps and enhances a POI workbook.
 */
class ExcelWorkbook
@JvmOverloads constructor(
    /**
     * Is used e. g. for getting number cell values as String.
     */
    val locale: Locale = Locale.getDefault()
) : AutoCloseable {

    lateinit var pOIWorkbook: Workbook
        private set
    private val sheetList = mutableListOf<ExcelSheet>()
    private var sheetListInitialized = false
    private val cellStyleMap: MutableMap<String, CellStyle?> = HashMap()
    private val fontMap: MutableMap<String, Font?> = HashMap()
    private var inputStream: InputStream? = null
    var filename: String? = null

    val filenameExtension: String?
        get() = File(filename ?: "unkown.xlsx").extension

    val filenameWithoutExtension: String?
        get() = File(filename ?: "unkown.xlsx").nameWithoutExtension

    var formulaEvaluator: FormulaEvaluator? = null
        get() {
            if (field == null) {
                field = pOIWorkbook.creationHelper.createFormulaEvaluator()
            }
            return field
        }
        private set

    @JvmOverloads
    constructor(
        workbook: Workbook,
        locale: Locale = Locale.getDefault()
    )
            : this(locale) {
        pOIWorkbook = workbook
    }

    @JvmOverloads
    constructor(
        excelFilename: String,
        locale: Locale = Locale.getDefault()
    )
            : this(File(excelFilename), locale)

    @JvmOverloads
    constructor(
        excelFile: File,
        locale: Locale = Locale.getDefault()
    )
            : this(locale) {
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
    @JvmOverloads
    constructor(
        inputStream: InputStream,
        filename: String,
        locale: Locale = Locale.getDefault()
    )
            : this(locale) {
        open(inputStream, filename)
    }

    /**
     * @param inputStream The input stream to read the Excel content from.
     * @param filename    Only for logging purposes if any error occurs.
     */
    @JvmOverloads
    constructor(
        byteArray: ByteArray,
        filename: String,
        locale: Locale = Locale.getDefault()
    )
            : this(locale) {
        open(byteArray.inputStream(), filename)
    }

    private fun open(inputStream: InputStream, filename: String) {
        this.filename = File(filename).name
        this.inputStream = inputStream
        try {
            pOIWorkbook = WorkbookFactory.create(inputStream)
        } catch (ex: IOException) {
            log.error("Couldn't open File '" + filename + "' from InputStream: " + ex.message, ex)
            throw RuntimeException(ex)
        }
    }

    fun sheetIterator(): MutableIterator<ExcelSheet> {
        initializeSheetList()
        return sheetList.iterator()
    }

    fun getSheet(idx: Int): ExcelSheet {
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

    fun createOrGetSheet(sheetName: String): ExcelSheet {
        initializeSheetList()
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

    /**
     * Clones the current sheet.
     *
     * @see Workbook.cloneSheet
     */
    fun cloneSheet(sheetNum: Int, name: String?): ExcelSheet? {
        val index = pOIWorkbook.numberOfSheets
        val poiSheet: Sheet = this.pOIWorkbook.cloneSheet(sheetNum)
        this.pOIWorkbook.setSheetName(index, name)
        val sheet = ExcelSheet(this, poiSheet)
        synchronized(this) {
            sheetListInitialized = false
            sheetList.clear()
        }
        return sheet
    }

    /**
     * Remove the sheet at the given position.
     *
     * @param index
     * @return this for chaining.
     */
    fun removeSheetAt(idx: Int) {
        pOIWorkbook.removeSheetAt(idx)
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
     * @param id Id of the cell style for re-usage. If not given, cell style will not saved for re-usage.
     * @return The CellStyle to use.
     */
    fun createOrGetCellStyle(id: String? = null): CellStyle {
        var cellStyle = cellStyleMap[id]
        if (cellStyle == null) {
            cellStyle = pOIWorkbook.createCellStyle()!!
            if (id != null) {
                cellStyleMap[id] = cellStyle
            }
        }
        return cellStyle
    }

    fun ensureCellStyle(format: ExcelCellStandardFormat): CellStyle {
        val exist = doesCellStyleExist("DataFormat." + format.name)
        val cellStyle = createOrGetCellStyle("DataFormat." + format.name)
        if (!exist) {
            if (format == ExcelCellStandardFormat.FLOAT) {
                cellStyle.dataFormat = getDataFormat(configuration.floatFormat)
            } else if (format == ExcelCellStandardFormat.INT) {
                val builtinFormat = BuiltinFormats.getBuiltinFormat(configuration.intFormat)
                cellStyle.dataFormat = if (builtinFormat >= 0) {
                    builtinFormat.toShort()
                } else {
                    getDataFormat(configuration.intFormat)
                }
            } else require(format != ExcelCellStandardFormat.DATE) { "Please call ensureDateCellStyle instead of ensureCellStyle." }
        }
        return cellStyle
    }

    fun ensureDateCellStyle(dateFormat: String): CellStyle {
        val exist = doesCellStyleExist("DataFormat." + ExcelCellStandardFormat.DATE.name + "." + dateFormat)
        val cellStyle = createOrGetCellStyle("DataFormat." + ExcelCellStandardFormat.DATE.name + "." + dateFormat)
        if (!exist) {
            cellStyle.dataFormat = getDataFormat(dateFormat)
        }
        return cellStyle
    }

    fun createDataFormat(): DataFormat {
        return pOIWorkbook.creationHelper.createDataFormat()
    }

    /**
     * Gets standard cell styles for dates (LocalDate, LocalDateTime, Calendar and Date) or creates them, if not exists.
     * Uses the default date formats of #standardFormats.
     */
    fun ensureStandardCellStyle(value: Any?): CellStyle? {
        value ?: return null
        val cellStyle = createOrGetCellStyle("StandardDateFormat.${value.javaClass}")
        when (value) {
            is LocalDate -> {
                cellStyle.dataFormat = getDataFormat(configuration.dayFormat)
            }
            is Date, is LocalDateTime, is Calendar -> {
                cellStyle.dataFormat = getDataFormat(configuration.dateTimeFormat)
            }
            else -> {
                log.warn("createCellStyle does only support LocalDate, LocalDateTime, Calendar and Date, but received: ${value.javaClass}.")
            }
        }
        return cellStyle
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

    fun setActiveSheet(sheetIdx: Int) {
        pOIWorkbook.setActiveSheet(sheetIdx)
    }

    fun setActiveSheet(sheetName: String) {
        val sheet = getSheet(sheetName) ?: return
        setActiveSheet(sheet.sheetIndex)
    }

    fun write(out: OutputStream) {
        pOIWorkbook.write(out)
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

    /**
     * Please note: if no sheet is specified, the first sheet is assumed.
     * @param reference Cell as Excel String: A1, A2, ... (also values such as "Sheet1!A1" and "$B$72)
     * @param type      Only used, if new cell will be created.
     * @return The (created) cell, not null.
     * @see [CellReference]
     */
    @JvmOverloads
    fun getCell(reference: String, type: ExcelCellType? = null): ExcelCell {
        return getSheet(0).getCell(reference, type)
    }

    val configuration = Configuration()

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
