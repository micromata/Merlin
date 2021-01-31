package de.micromata.merlin.smarthome.examples.homeassistant_knx.data

import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val log = KotlinLogging.logger {}

class DataStorage private constructor() {
    private val knxItems: MutableList<KnxItem> = ArrayList()

    // Contains all knx items:
    private val knxItemRegistryMap = mutableMapOf<String, KnxItem>()

    val dateTime: String
        get() {
            val date = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return date.format(formatter)
        }

    fun add(item: KnxItem) {
        if (knxItemRegistryMap.containsKey(item.fullName)) {
            log.warn("KNX item added twice: id='${item.fullName}'")
        }
        knxItems.add(item)
        knxItemRegistryMap.put(item.fullName, item)
    }

    fun filterItems(category: KnxItemCategory): List<KnxItem> {
        return knxItems.filter { it.category == category }
    }

    companion object {
        val instance = DataStorage()
    }
}
