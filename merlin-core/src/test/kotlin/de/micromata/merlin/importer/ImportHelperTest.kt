package de.micromata.merlin.importer

import de.micromata.merlin.Definitions
import de.micromata.merlin.excel.ExcelWorkbook
import de.micromata.merlin.excel.importer.ImportHelper
import org.apache.poi.ss.usermodel.Row
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*

internal class ImportHelperTest {
    class Person(var email: String? = null,
                 var name: String? = null,
                 var age: Int? = null,
                 var date: LocalDate? = null,
                 var dateTime: LocalDateTime? = null,
                 var money: Double? = null,
                 var height: BigDecimal? = null,
                 var floatValue: Float? = null,
                 var text: String? = null)

    @Test
    fun fillBeanTest() {
        val excelWorkbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Workbook-Test.xlsx"), Locale.GERMAN)
        val sheet = excelWorkbook.getSheet("fillBean")!!
        sheet.registerColumn("E-Mail").setTargetProperty("email")
        sheet.registerColumn("Name")
        sheet.registerColumn("Age")
        sheet.registerColumn("Date")
        sheet.registerColumn("DateTime")
        sheet.registerColumn("Money")
        sheet.registerColumn("Height")
        sheet.registerColumn("FloatValue")
        sheet.registerColumn("Text")
        sheet.registerColumn("unknown")
        val it: Iterator<Row> = sheet.dataRowIterator
        var row = it.next()
        var person = Person()
        ImportHelper.fillBean(person, sheet, row.rowNum)
        Assertions.assertEquals("k.reinhard@acme.com", person.email)
        Assertions.assertEquals("Kai Reinhard", person.name)
        Assertions.assertEquals(48, person.age)
        Assertions.assertEquals(37.25, person.money)
        Assertions.assertEquals("1.78", person.height!!.toString())
        Assertions.assertEquals("1.27", person.floatValue!!.toString())
        Assertions.assertEquals("1,3", person.text)

        Assertions.assertEquals(2019, person.date!!.year)
        Assertions.assertEquals(28, person.date!!.dayOfMonth)
        Assertions.assertEquals(Month.DECEMBER, person.date!!.month)

        Assertions.assertEquals(2020, person.dateTime!!.year)
        Assertions.assertEquals(1, person.dateTime!!.dayOfMonth)
        Assertions.assertEquals(Month.JANUARY, person.dateTime!!.month)
        Assertions.assertEquals(23, person.dateTime!!.hour)
        Assertions.assertEquals(55, person.dateTime!!.minute)
        Assertions.assertEquals(0, person.dateTime!!.second)
    }
}
