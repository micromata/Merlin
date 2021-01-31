package de.micromata.merlin.smarthome.examples.homeassistant_knx.data

import de.micromata.merlin.data.Data

class KnxItem : Data("KnxItem") {
    val id: String?
        get() = getString("Id")

    val category: KnxItemCategory?
        get() = KnxItemCategory.from(categoryString)

    val name: String?
        get() = getString("Name")

    /**
     * Optional suffix for different entries for same item etc.
     */
    val nameSuffix: String?
        get() = getString("Name suffix")

    val fullName: String
        get() {
            return if (nameSuffix.isNullOrBlank()) {
                name ?: ""
            } else {
                "${name ?: ""} $nameSuffix"
            }
        }

    val categoryString: String?
        get() = getString("Category")

    val syncState: String?
        get() = getString("Sync state")

    val syncStateAsLine: String
        get() = asLine("sync_state", syncState)

    /**
     * Optional type: temperature, ...
     */
    val type: String?
        get() = getString("Type")

    val typeAsLine: String
        get() = asLine("type", type)

    /**
     * Optional type: temperature, ...
     */
    val deviceClass: String?
        get() = getString("Device class")

    val deviceClassAsLine: String
        get() = asLine("device_class", deviceClass)

    val knxAddress: String?
        get() = getString("KNX-Address")

    val knxAddressWithSlashes: String
        get() = knxAddress?.replace('.', '/') ?: ""

    private fun asLine(key: String, value: String?): String {
        return if (value.isNullOrBlank()) {
            "# $key: none"
        } else {
            return "$key: '$value'"
        }

    }
}
