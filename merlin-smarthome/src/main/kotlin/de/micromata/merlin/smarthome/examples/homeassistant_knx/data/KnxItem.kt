package de.micromata.merlin.smarthome.examples.homeassistant_knx.data

import de.micromata.merlin.data.Data
import de.micromata.merlin.utils.ReplaceUtils
import org.apache.commons.lang3.StringUtils

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

    val entityName: String
        get() {
            val sb = StringBuilder()
            val charArray = StringUtils.stripAccents(fullName).toCharArray()
            for (i in charArray.indices) {
                val ch = charArray[i]
                if (ALLOWED_ENTITY_CHARS.indexOf(ch) >= 0) {
                    sb.append(ch)
                } else {
                    sb.append("_")
                }
            }
            return "${category?.name}.$sb".toLowerCase()
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

    val knxStateAddress: String?
        get() = getString("KNX-State-Address")

    val knxStateAddressWithSlashes: String
        get() = knxStateAddress?.replace('.', '/') ?: ""

    /**
     * Optional flag Invert, ...
     */
    val invert: Boolean?
        get() = getBoolean("Invert")

    val invertAsLine: String
        get() {
            return if (invert == true) {
                "invert: true"
            } else {
                "# invert: false"
            }
        }

    val area: String?
        get() = getString("Area")

    /**
     * Optional flag Invert, ...
     */
    val stateFilter: Boolean?
        get() = getBoolean("State filter")

    private fun asLine(key: String, value: String?): String {
        return if (value.isNullOrBlank()) {
            "# $key: none"
        } else {
            return "$key: '$value'"
        }
    }

    companion object {
        const val ALLOWED_ENTITY_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._"
    }
}
