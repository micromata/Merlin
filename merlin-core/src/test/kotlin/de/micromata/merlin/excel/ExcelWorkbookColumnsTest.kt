package de.micromata.merlin.excel

import de.micromata.merlin.CoreI18n
import de.micromata.merlin.Definitions
import org.apache.poi.ss.usermodel.Row
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

internal class ExcelWorkbookColumnsTest {
    private val log = LoggerFactory.getLogger(ExcelWorkbookColumnsTest::class.java)
    @Test
    fun configReaderValidationTest() {
        val coreI18N = CoreI18n.setDefault(Locale.ROOT)
        val excelWorkbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Workbook-Test.xlsx"))
        val excelSheet = excelWorkbook.getSheet("sheet 1")!!
        excelSheet.registerColumn("E-Mail")
        excelSheet.registerColumn("Name")
        excelSheet.registerColumn("Age").addColumnListener(ExcelColumnNumberValidator())
        excelSheet.registerColumn("Date").addColumnListener(ExcelColumnDateValidator())
        val email2ColDef = excelSheet.registerColumn("E-Mail")

        val it: Iterator<Row> = excelSheet.dataRowIterator
        var row = it.next()
        Assertions.assertEquals("k.reinhard@acme.com", excelSheet.getCellString(row, "E-Mail"))
        Assertions.assertEquals("k.reinhard@home.org", excelSheet.getCellString(row, email2ColDef))
        Assertions.assertEquals("Kai Reinhard", excelSheet.getCellString(row, "Name"))
        Assertions.assertEquals(1, excelSheet.getColumnDef("E-Mail")!!.columnNumber)
        Assertions.assertEquals(5, email2ColDef.columnNumber)
    }
}
