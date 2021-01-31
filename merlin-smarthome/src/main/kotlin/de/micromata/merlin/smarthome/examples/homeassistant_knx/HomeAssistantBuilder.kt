package de.micromata.merlin.smarthome.examples.homeassistant_knx

import de.micromata.merlin.excel.ExcelWorkbook
import de.micromata.merlin.smarthome.examples.homeassistant_knx.data.DataStorage
import de.micromata.merlin.smarthome.examples.homeassistant_knx.data.KnxItemCategory
import de.micromata.merlin.velocity.VelocityHelper
import mu.KotlinLogging
import org.apache.velocity.VelocityContext
import java.io.File
import kotlin.system.exitProcess

private val log = KotlinLogging.logger {}

class HomeAssistantBuilder {
    fun run(xlsFile: String) {
        val excelWorkbook = ExcelWorkbook(xlsFile)
        KnxItemsReader().readKNXItems(excelWorkbook)

        val category = KnxItemCategory.BINARY_SENSOR
        val items = DataStorage.instance.filterItems(category)
        val file = "knx_binary_sensor.yaml"
        val context = VelocityContext()
        context.put("data", DataStorage.instance)
        context.put("items", items)
        val templateDir = File("merlin-smarthome/examples/homeassistant-knx/")
        log.info { "Processing ${items.size} items..." }
        VelocityHelper.merge(templateDir, file, File("."), context)

        /*
        val templateDir = File("merlin-smarthome/examples/openhab-knx/")
        val outDir = File("merlin-smarthome/out/examples/openhab-knx")
        val configDir = createDir(outDir, "config")
        val itemsDir = createDir(configDir, "items")
        val persistenceDir = createDir(configDir, "persistence")
        val sitemapsDir = createDir(configDir, "sitemaps")
        val thingsDir = createDir(configDir, "things")
        VelocityHelper.merge(templateDir, "knx.things", thingsDir, context)
        VelocityHelper.merge(templateDir, "knx.items", itemsDir, context)
        VelocityHelper.merge(templateDir, "zoneminder.things", thingsDir, context)
        VelocityHelper.merge(templateDir, "jdbc.persist", persistenceDir, context)
        VelocityHelper.merge(templateDir, "home.sitemap", sitemapsDir, context)*/
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 1) {
                log.error("Please attach Excel-file to parse as single argument.")
                exitProcess(0)
            }
            val filename = args[0]
            val xlsFile = File(filename)
            if (!xlsFile.canRead()) {
                log.error("Can't read xls file '${xlsFile.absoluteFile}'.")
                exitProcess(0)
            }
            log.info { "Reading xls file: '${xlsFile.absoluteFile}'..." }
            HomeAssistantBuilder().run(filename)
        }
    }
}
