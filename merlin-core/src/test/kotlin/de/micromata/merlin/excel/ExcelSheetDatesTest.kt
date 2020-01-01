package de.micromata.merlin.excel

import de.micromata.merlin.Definitions
import org.apache.poi.ss.usermodel.Row
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*

internal class ExcelSheetDatesTest {
    @Test
    fun parseDatesTest() {
        val excelWorkbook = ExcelWorkbook(File(Definitions.EXAMPLES_EXCEL_TEST_DIR, "Workbook-Test.xlsx"))
        val sheet = excelWorkbook.getSheet("dates")!!
        val dateVal = ExcelColumnDateValidator(Locale.ENGLISH, *ExcelColumnDateValidator.GERMAN_DATE_FORMATS)
        val dateTimeVal = ExcelColumnDateValidator(Locale.ENGLISH, *ExcelColumnDateValidator.GERMAN_DATETIME_FORMATS)
        sheet.registerColumn("Date", dateVal)
        sheet.registerColumn("DateTime", dateVal)

        val it: Iterator<Row> = sheet.dataRowIterator
        var row = it.next()
        checkDate(2020, Month.JANUARY, 1, dateVal.getLocalDate(sheet.getCell(row, "Date")))
        checkDateTime(2020, Month.JANUARY, 1, 16, 25, 23,
                dateTimeVal.getLocalDateTime(sheet.getCell(row, "DateTime")))
        row = it.next()
        checkDate(2020, Month.FEBRUARY, 29, dateVal.getLocalDate(sheet.getCell(row, "Date")))
        checkDateTime(2020, Month.FEBRUARY, 29, 23, 59, 17,
                dateTimeVal.getLocalDateTime(sheet.getCell(row, "DateTime")))
        row = it.next()
        checkDate(2020, Month.FEBRUARY, 2, dateVal.getLocalDate(sheet.getCell(row, "Date")))
        checkDateTime(2020, Month.FEBRUARY, 2, 0, 0, 17,
                dateTimeVal.getLocalDateTime(sheet.getCell(row, "DateTime")))
        row = it.next()
        checkDate(2020, Month.FEBRUARY, 29, dateVal.getLocalDate(sheet.getCell(row, "Date")))
        checkDateTime(2020, Month.FEBRUARY, 29, 5, 23, 0,
                dateTimeVal.getLocalDateTime(sheet.getCell(row, "DateTime")))
        row = it.next()
        checkDate(2020, Month.NOVEMBER, 21, dateVal.getLocalDate(sheet.getCell(row, "Date")))
        checkDateTime(2020, Month.NOVEMBER, 21, 1, 2, 0,
                dateTimeVal.getLocalDateTime(sheet.getCell(row, "DateTime")))
        row = it.next()
        checkDate(2020, Month.NOVEMBER, 21, dateVal.getLocalDate(sheet.getCell(row, "Date")))
        checkDateTime(2020, Month.NOVEMBER, 21, 1, 2, 17,
                dateTimeVal.getLocalDateTime(sheet.getCell(row, "DateTime")))
    }

    private fun checkDateTime(expectedYear: Int, expectedMonth: Month, expectedDayOfMonth: Int, expectedHour: Int, expectedMinute: Int, expectedSecond: Int, date: LocalDateTime?) {
        checkDate(expectedYear, expectedMonth, expectedDayOfMonth, date?.toLocalDate())
        Assertions.assertEquals(expectedHour, date!!.hour)
        Assertions.assertEquals(expectedMinute, date.minute)
        Assertions.assertEquals(expectedSecond, date.second)
        Assertions.assertEquals(0, date.nano)
    }

    private fun checkDate(expectedYear: Int, expectedMonth: Month, expectedDayOfMonth: Int, date: LocalDate?) {
        Assertions.assertNotNull(date)
        Assertions.assertEquals(expectedYear, date!!.year)
        Assertions.assertEquals(expectedMonth, date.month)
        Assertions.assertEquals(expectedDayOfMonth, date.dayOfMonth)
    }
}
