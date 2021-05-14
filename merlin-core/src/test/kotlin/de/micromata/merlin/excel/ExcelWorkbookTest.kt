package de.micromata.merlin.excel

import de.micromata.merlin.CoreI18n
import de.micromata.merlin.Definitions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

internal class ExcelWorkbookTest {
    private val log = LoggerFactory.getLogger(ExcelWorkbookTest::class.java)

    @Test
    fun cellReferenceTest() {
        val workbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Test.xlsx"))
        Assertions.assertEquals("Surname", workbook.getCell("A3").stringCellValue)
        Assertions.assertEquals("Property", workbook.getCell("Config!A1").stringCellValue)
        val sheet = workbook.getSheet(2)
        Assertions.assertEquals("Property", sheet.getCell("A1").stringCellValue)
    }

    @Test
    fun configReaderValidationTest() {
        val coreI18N = CoreI18n.setDefault(Locale.ROOT)
        val excelWorkbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Test.xlsx"))
        val configReader = ExcelConfigReader(
            excelWorkbook.getSheet("Config"),
            "Property", "Value"
        )
        val props = configReader.readConfig(excelWorkbook)
        Assertions.assertTrue(configReader.sheet.hasValidationErrors())
        val validationErrors = configReader.sheet.allValidationErrors
        Assertions.assertEquals(1, validationErrors.size)
        Assertions.assertEquals(
            "In sheet 'Config', column A:'Property' and row #5: Cell value isn't unique. It's already used in row #3: 'user'.",
            validationErrors.iterator().next().getMessageWithAllDetails(coreI18N)
        )
        Assertions.assertEquals("horst", props.getConfigString("user"))
        Assertions.assertEquals("Hamburg", props.getConfigString("city"))
    }

    @Test
    @Throws(IOException::class)
    fun validationExcelResponseTest() {
        validationexcelResponseTest(CoreI18n.getDefault(), "")
        validationexcelResponseTest(CoreI18n.setDefault(Locale.GERMAN), "_de")
    }

    @Throws(IOException::class)
    private fun validationexcelResponseTest(coreI18N: CoreI18n, fileSuffix: String) {
        val excelWorkbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Test.xlsx"))
        val configReader = ExcelConfigReader(
            excelWorkbook.getSheet("Config"),
            "Property", "Value"
        )
        val props = configReader.readConfig(excelWorkbook)
        Assertions.assertTrue(configReader.sheet.hasValidationErrors())
        val ctx = ExcelWriterContext(coreI18N, excelWorkbook).setAddErrorColumn(true)
        configReader.sheet.markErrors(ctx)
        var sheet = excelWorkbook.getSheet("Validator-Test")
        sheet!!.registerColumn("Name", ExcelColumnValidator().setRequired())
        sheet.registerColumn("Surname", ExcelColumnValidator().setRequired())
        sheet.registerColumn("Birthday", ExcelColumnDateValidator())
        sheet.registerColumn("City", ExcelColumnValidator())
        sheet.registerColumn("E-Mail", ExcelColumnPatternValidator().setEMailPattern().setRequired().setUnique())
        sheet.registerColumn("Number", ExcelColumnValidator().setUnique())
        sheet.registerColumn("Country", ExcelColumnValidator())
        sheet.markErrors(ctx)
        sheet = excelWorkbook.getSheet("Validator-Mass-Test")
        sheet!!.registerColumn("Name", ExcelColumnValidator().setUnique())
        sheet.registerColumn("Birthday", ExcelColumnDateValidator())
        sheet.registerColumn("City", ExcelColumnValidator())
        sheet.registerColumn("E-Mail", ExcelColumnPatternValidator().setEMailPattern().setRequired())
        sheet.registerColumn("Country", ExcelColumnValidator())
        sheet.markErrors(ctx)
        sheet = excelWorkbook.getSheet("Clean-Test")
        sheet!!.registerColumn("Name", ExcelColumnValidator().setUnique())
        sheet.registerColumn("Birthday", ExcelColumnDateValidator())
        sheet.registerColumn("City", ExcelColumnValidator())
        sheet.registerColumn("E-Mail", ExcelColumnPatternValidator().setEMailPattern().setRequired())
        sheet.registerColumn("Country", ExcelColumnValidator())
        sheet.markErrors(ctx)
        sheet = excelWorkbook.getSheet("Empty-sheet")
        sheet!!.registerColumn("Name", ExcelColumnValidator().setUnique())
        sheet.registerColumn("E-Mail", ExcelColumnPatternValidator().setEMailPattern().setRequired())
        sheet.markErrors(ctx)
        sheet = excelWorkbook.getSheet("Empty-sheet2")
        sheet!!.registerColumn("Name", ExcelColumnValidator().setUnique())
        sheet.registerColumn("E-Mail", ExcelColumnPatternValidator().setEMailPattern().setRequired())
        sheet.markErrors(ctx)
        val file = File(Definitions.OUTPUT_DIR, "Test-result$fileSuffix.xlsx")
        log.info("Writing modified Excel file: " + file.absolutePath)
        excelWorkbook.pOIWorkbook.write(FileOutputStream(file))
    }
}
