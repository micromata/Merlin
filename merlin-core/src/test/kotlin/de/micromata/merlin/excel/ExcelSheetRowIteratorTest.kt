package de.micromata.merlin.excel

import de.micromata.merlin.Definitions
import org.apache.poi.ss.usermodel.Row
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Month

internal class ExcelSheetRowIteratorTest {
    @Test
    fun datarowIteratorTest() {
        val excelWorkbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Workbook-Test.xlsx"))
        val excelSheet = excelWorkbook.getSheet("empty rows")!!
        excelSheet.registerColumn("E-Mail")
        excelSheet.registerColumn("Name")
        excelSheet.registerColumn("Age")
        excelSheet.registerColumn("Date")
        val it: Iterator<Row> = excelSheet.dataRowIterator
        var row = it.next()
        Assertions.assertEquals("k.reinhard@acme.com", excelSheet.getCellString(row, "E-Mail"))
        Assertions.assertTrue(it.hasNext())
        row = it.next()
        Assertions.assertEquals("b.muster@acme.com", excelSheet.getCellString(row, "E-Mail"))
        row = it.next()
        Assertions.assertEquals("test", PoiHelper.getValueAsString(excelSheet.getCell(row.rowNum, 1)))
        row = it.next()
        val date = ExcelColumnDateValidator().getDate(excelSheet.getCell(row, "Date"))!!
        Assertions.assertEquals(1, date.dayOfMonth)
        Assertions.assertEquals(Month.JANUARY, date.month)
        Assertions.assertEquals(2020, date.year)
        row = it.next()
        Assertions.assertEquals("Test", excelSheet.getCellString(row, "E-Mail"))
        Assertions.assertFalse(it.hasNext())
    }

    @Test
    fun datarowIteratorColumnsTest() {
        val excelWorkbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Workbook-Test.xlsx"))
        val excelSheet = excelWorkbook.getSheet("empty rows")!!
        excelSheet.registerColumn("E-Mail")
        excelSheet.registerColumn("Name")
        excelSheet.registerColumn("Age")
        excelSheet.registerColumn("Date")
        excelSheet.setColumnsForRowEmptyCheck("E-Mail", "Name", "Age")
        val it: Iterator<Row> = excelSheet.dataRowIterator
        var row = it.next()
        Assertions.assertEquals("k.reinhard@acme.com", excelSheet.getCellString(row, "E-Mail"))
        Assertions.assertTrue(it.hasNext())
        row = it.next()
        Assertions.assertEquals("b.muster@acme.com", excelSheet.getCellString(row, "E-Mail"))
        row = it.next()
        Assertions.assertEquals("Test", excelSheet.getCellString(row, "E-Mail"))
        Assertions.assertFalse(it.hasNext())
    }
}
