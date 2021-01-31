package de.micromata.merlin.smarthome.examples.homeassistant_knx

import de.micromata.merlin.excel.ExcelSheet
import de.micromata.merlin.excel.ExcelWorkbook
import de.micromata.merlin.smarthome.examples.homeassistant_knx.data.DataStorage
import de.micromata.merlin.smarthome.examples.homeassistant_knx.data.KnxItem
import mu.KotlinLogging
import org.apache.poi.ss.usermodel.Row

private val log = KotlinLogging.logger {}

class KnxItemsReader {
    fun readKNXItems(workbook: ExcelWorkbook) {
        workbook.sheetIterator().forEach {
            readKNXItems(it)
        }

    }

    private fun readKNXItems(sheet: ExcelSheet) {
        sheet.registerColumns(
            "Category",
            "Type",
            "Device class",
            "KNX-Address",
            "Sync state",
            "Name",
            "Name suffix"
        )
        sheet.analyze(true)
        if (sheet.hasValidationErrors()) {
            for (msg in sheet.allValidationErrors) {
                log.error(msg.getMessage())
            }
            log.error("*** Aborting processing of knx things due to validation errors (see above).")
            return
        }
        log.info("Reading Excel sheet '${sheet.sheetName}'...")
        var counter = 0
        val it: Iterator<Row> = sheet.dataRowIterator
        while (it.hasNext()) {
            val row = it.next()
            val item = KnxItem()
            sheet.readRow(row, item)
            if (item.category == null || item.name.isNullOrBlank()) {
                log.info { "Skipping item in row ${row.rowNum} without empty category or name: category=${item.category}, name=${item.fullName}" }
            } else {
                DataStorage.instance.add(item)
                counter++
            }
        }
        log.info("Number of read KNX items in sheet '$SHEET_NAME': $counter")
    }

    companion object {
        private const val SHEET_NAME = "KNX-Items"
    }
}
