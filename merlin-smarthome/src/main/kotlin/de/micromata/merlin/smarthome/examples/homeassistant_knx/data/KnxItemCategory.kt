package de.micromata.merlin.smarthome.examples.homeassistant_knx.data

enum class KnxItemCategory(val id: String) {
    SENSOR("sensor"),
    BINARY_SENSOR("binary_sensor"),
    LIGHT("light"),
    SWITCH("switch");

    companion object {
        fun from(id: String?): KnxItemCategory? {
            if (id.isNullOrBlank()) {
                return null
            }
            values().forEach {
                if (it.id == id.toLowerCase().trim()) {
                    return it
                }
            }
            return null
        }
    }
}
