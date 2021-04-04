package de.micromata.merlin.data

import org.apache.commons.lang3.StringUtils
import java.util.HashMap

open class Data(private val type: String) {
    private val properties: MutableMap<String, Any?> = HashMap()
    fun put(property: String?, value: Any?) {
        properties[property!!] = value
        if (value != null) {
            val maxLength = getMaxLength(type, property)
            val length = value.toString().length
            if (length > maxLength) {
                setMaxLength(type, property, length)
            }
        }
    }

    fun getString(property: String): String? {
        val value = getValue(property)
        return value?.toString()
    }

    fun getRightPadString(property: String): String {
        return getRightPadString(property, 0)
    }

    fun getRightPadString(prefix: String?, property: String, suffix: String?): String {
        val offset = StringUtils.length(prefix) + StringUtils.length(suffix)
        val str = getString(property)
        return StringUtils.rightPad(
            StringUtils.defaultString(prefix) + str + StringUtils.defaultString(suffix), getMaxLength(
                type, property
            ) + offset
        )
    }

    /**
     * @param property the property for [.getString].
     * @param offset offset for [StringUtils.rightPad]
     * @return Formatted string with fixed length of length of longest property of this type.
     */
    fun getRightPadString(property: String, offset: Int): String {
        val str = getString(property)
        return StringUtils.rightPad(
            str, getMaxLength(
                type, property
            ) + offset
        )
    }

    fun getMaxLength(property: String): Int {
        return getMaxLength(type, property)
    }

    fun getValue(property: String): Any? {
        return properties[property]
    }

    fun getBoolean(property: String): Boolean? {
        val value = getString(property) ?: return null
        return value.toLowerCase() in arrayOf("1", "y", "yes", "t", "true", "ja", "j")
    }

    companion object {
        // Stores max length of all properties of same type for formatting output (rightPad).
        private val maxPropertyLength: MutableMap<String, Int> = HashMap()
        private fun getMaxLength(type: String, property: String): Int {
            val maxLength = maxPropertyLength["$type.$property"]
            return maxLength ?: 0
        }

        private fun setMaxLength(type: String, property: String, maxLength: Int) {
            maxPropertyLength["$type.$property"] = maxLength
        }
    }
}
