package de.micromata.merlin.excel

import de.micromata.merlin.Definitions
import org.apache.poi.ss.usermodel.Row
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File

internal class ExcelSheetRowIteratorTest {
    private val log = LoggerFactory.getLogger(ExcelSheetRowIteratorTest::class.java)
    @Test
    fun configReaderValidationTest() {
        val excelWorkbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Workbook-Test.xlsx"))
        val excelSheet = excelWorkbook.getSheet("empty rows")!!
        excelSheet.registerColumn("E-Mail")
        excelSheet.registerColumn("Name")
        excelSheet.registerColumn("Age")
        excelSheet.registerColumn("Date")
        val email2ColDef = excelSheet.registerColumn("E-Mail")
        val it: Iterator<Row> = excelSheet.dataRowIterator
        var row = it.next()
        Assertions.assertEquals("k.reinhard@acme.com", excelSheet.getCellString(row, "E-Mail"))
        Assertions.assertTrue(it.hasNext())
        row = it.next()
        Assertions.assertEquals("b.muster@acme.com", excelSheet.getCellString(row, "E-Mail"))
        row = it.next()
        Assertions.assertEquals("test", PoiHelper.getValueAsString(excelSheet.getCell(row.rowNum, 1)))
        Assertions.assertFalse(it.hasNext())
    }
}
