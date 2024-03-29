package de.micromata.merlin.smarthome.examples.homeassistant_knx

import de.micromata.merlin.excel.ExcelWorkbook
import de.micromata.merlin.smarthome.examples.homeassistant_knx.data.DataStorage
import de.micromata.merlin.smarthome.examples.homeassistant_knx.data.KnxItem
import de.micromata.merlin.smarthome.examples.homeassistant_knx.data.KnxItemCategory
import de.micromata.merlin.velocity.VelocityHelper
import mu.KotlinLogging
import org.apache.velocity.VelocityContext
import java.io.File
import kotlin.system.exitProcess

private val log = KotlinLogging.logger {}

class HomeAssistantBuilder(val templateDir: File) {
    private val outDir: File = File("out")

    init {
        if (!outDir.exists()) {
            log.info("Creating output directory '${outDir.absolutePath}.")
            outDir.mkdir()
        }
        log.info("Using output directory '${outDir.absolutePath}.")
    }

    private fun run(xlsFile: String) {
        val excelWorkbook = ExcelWorkbook(xlsFile)
        KnxItemsReader().readKNXItems(excelWorkbook)

        createEntityYaml(KnxItemCategory.BINARY_SENSOR, "knx_binary_sensor.yaml")
        createEntityYaml(KnxItemCategory.SENSOR, "knx_sensor.yaml")
        createEntityYaml(KnxItemCategory.SWITCH, "knx_switch.yaml")
        createEntityYaml(KnxItemCategory.LIGHT, "knx_light.yaml")

        createLovelaceYaml("UG")
        createLovelaceYaml("EG")
        createLovelaceYaml("OG")
        createLovelaceYaml("NB")
        createLovelaceYaml("Garage")
        createLovelaceYaml("UG", "EG", "OG", "NB", "Garage")
        createLovelaceYaml("BWM")

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

    private fun createEntityYaml(category: KnxItemCategory, templateFile: String) {
        val items = DataStorage.instance.filterItems(category)
        write(items, templateFile, templateFile)
    }

    private fun createLovelaceYaml(vararg areas: String) {
        val origItems = DataStorage.instance.filterItems(KnxItemCategory.BINARY_SENSOR)
            .filter { it.stateFilter == true }
        val items = mutableListOf<KnxItem>()
        areas.forEach { area ->
            origItems.forEach { item ->
                if (item.area == area) {
                    items.add(item)
                }
            }
        }
        write(items, "ui_lovelace_area_EMA.yaml", "ui_lovelace_${areas.joinToString("_")}_EMA.yaml")
    }

    private fun write(items: List<KnxItem>, templateFile: String, outFile: String) {
        val context = VelocityContext()
        context.put("data", DataStorage.instance)
        context.put("items", items)
        log.info { "Processing ${items.size} items..." }
        VelocityHelper.merge(File(templateDir, templateFile), File(outDir, outFile), context)

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size < 1 || args.size > 2) {
                log.error("Please call with optional template dir as first argument and Excel-file to parse as seconde argument: gradle run --args=\"<template-dir> <xls-file>\"")
                exitProcess(0)
            }
            val templateDir =
                if (args.size == 1) {
                    File("merlin-smarthome/examples/homeassistant-knx/")
                } else {
                    File(args[0])
                }
            if (!templateDir.exists()) {
                log.error("Template directory '${templateDir.absolutePath}' doesn't exist.")
                exitProcess(0)
            }
            log.info("Using template directory: ${templateDir.absolutePath}")
            val filename = args[args.size - 1]
            val xlsFile = File(filename)
            if (!xlsFile.canRead()) {
                log.error("Can't read xls file '${xlsFile.absoluteFile}'.")
                exitProcess(0)
            }
            log.info { "Reading xls file: '${xlsFile.absoluteFile}'..." }
            HomeAssistantBuilder(templateDir).run(filename)
        }
    }
}
