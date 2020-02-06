package de.micromata.merlin.excel

import de.micromata.merlin.Definitions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream

internal class ExcelRowActionsTest {
    @Test
    fun copyInsertTest() {
        val workbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Workbook-Test.xlsx"))
        val sheet1 = workbook.getSheet("sheet 1")!!
        val actionsSheet = workbook.getSheet("Row-actions")!!

        sheet1.getRow(2)!!.copyAndInsert(actionsSheet, 0)
        Assertions.assertEquals("k.reinhard@acme.com", actionsSheet.getRow(0)!!.getCell(1)!!.stringCellValue)
        sheet1.getRow(3)!!.copyAndInsert(actionsSheet, 1)
        Assertions.assertEquals("k.reinhard@acme.com", actionsSheet.getRow(0)!!.getCell(1)!!.stringCellValue)
        Assertions.assertEquals("b.muster@acme.com", actionsSheet.getRow(1)!!.getCell(1)!!.stringCellValue)
        val copiedRow = actionsSheet.getRow(1)!!
        Assertions.assertTrue(actionsSheet.poiSheet.mergedRegions?.any { it.firstRow == copiedRow.rowNum && it.lastRow == copiedRow.rowNum && it.firstColumn == 5 && it.lastColumn == 7 } == true)

        Assertions.assertEquals(0.0, actionsSheet.getRow(2)!!.getCell(0)!!.numericCellValue)
        Assertions.assertEquals(1.0, actionsSheet.getRow(3)!!.getCell(0)!!.numericCellValue)
        actionsSheet.shiftRows(3, n = 2)
        Assertions.assertEquals(0.0, actionsSheet.getRow(2)!!.getCell(0)!!.numericCellValue)
        Assertions.assertEquals(1.0, actionsSheet.getRow(5)!!.getCell(0)!!.numericCellValue)
        Assertions.assertEquals(2.0, actionsSheet.getRow(6)!!.getCell(0)!!.numericCellValue)
        Assertions.assertEquals(3.0, actionsSheet.getRow(7)!!.getCell(0)!!.numericCellValue)
        Assertions.assertEquals(4.0, actionsSheet.getRow(8)!!.getCell(0)!!.numericCellValue)

        val newRow = actionsSheet.getRow(5)!!.copyAndInsert()
        newRow.getCell(0)!!.setCellValue(1.5)
        Assertions.assertEquals(1.0, actionsSheet.getRow(5)!!.getCell(0)!!.numericCellValue)
        Assertions.assertEquals(1.5, actionsSheet.getRow(6)!!.getCell(0)!!.numericCellValue)
        Assertions.assertEquals(2.0, actionsSheet.getRow(7)!!.getCell(0)!!.numericCellValue)
        Assertions.assertTrue(actionsSheet.poiSheet.mergedRegions?.any { it.firstRow == newRow.rowNum && it.lastRow == newRow.rowNum && it.firstColumn == 7 && it.lastColumn == 8 } == true)

        sheet1.getRow(3)!!.copyAndInsert(actionsSheet, 14)

        val file = File(Definitions.OUTPUT_DIR, "RowActionsTest.xlsx")
        log.info("Writing checksum Excel file: " + file.getAbsolutePath())
        workbook.write(FileOutputStream(file))


/*        workbook.asByteArrayOutputStream
        sheet1.getRow(2)
        sheet1.getRow(0)
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
        Assertions.assertFalse(it.hasNext())*/
    }

    private val log = LoggerFactory.getLogger(ExcelRowActionsTest::class.java)
}
