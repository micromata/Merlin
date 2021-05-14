package de.micromata.merlin.excel

import de.micromata.merlin.Definitions
import mu.KotlinLogging
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

private val log = KotlinLogging.logger {}

internal class ExcelSheetRowTest {
    class Person(
        val name: String
    ) {
        val birthday = LocalDate.of(1970, Month.JANUARY, 1)
        val zipCode = 1234
        val height = 27.78
    }

    @Test
    fun autoFillTest() {
        val workbook = ExcelWorkbook(XSSFWorkbook())
        val excelSheet = workbook.createOrGetSheet("autofill")
        excelSheet.registerColumn("Name")
        excelSheet.registerColumn("Zip code", "zipCode")
        excelSheet.registerColumn("Birthday")
        excelSheet.registerColumn("Height")
        excelSheet.createRow().fillHeadRow()
        var row = excelSheet.createRow()
        row.autoFillFromObject(Person("Kai"))
        row = excelSheet.createRow()
        row.autoFillFromObject(Person("Pete"), "birthday")
        Assertions.assertEquals("Kai", excelSheet.getCell("A2").stringCellValue)
        Assertions.assertEquals(1234, excelSheet.getCell("B2").intCellValue)
        Assertions.assertEquals(LocalDateTime.of(1970, Month.JANUARY, 1, 0 , 0), excelSheet.getCell("C2").getValue())
        Assertions.assertEquals(27.78, excelSheet.getCell("D2").getValue())

        Assertions.assertEquals("Pete", excelSheet.getCell("A3").stringCellValue)
        Assertions.assertEquals("", excelSheet.getCell("C3").getValue())

        // Create 3rd row and test protection of cell style:
        row = excelSheet.createRow()
        // Create cell C4 with own cell style (float):
        row.row.createCell(2,  CellType.NUMERIC).cellStyle = workbook.ensureCellStyle(ExcelCellStandardFormat.FLOAT)
        row.autoFillFromObject(Person("Hurzel"))
        Assertions.assertEquals("Hurzel", excelSheet.getCell("A4").stringCellValue)
        Assertions.assertEquals(25569.0, excelSheet.getCell("C4").getValue(), "date is expected as float, because cell style should be protected and not be set as date.")

        val file = File(Definitions.OUTPUT_DIR, "RowAutoFillTest.xlsx")
        log.info("Writing checksum Excel file: " + file.getAbsolutePath())
        workbook.write(FileOutputStream(file))
    }
}
