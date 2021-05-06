package de.micromata.merlin.excel

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Defines standard formats for dates.
 */
class StandardFormats {
    enum class TimeStampPrecision { DAY, MINUTE, SECOND }

    var dateFormat: String = "MM/dd/yyyy"
        set(value) {
            field = value
            dateTimeFormat = "$dateFormat hh:mm"
        }
    lateinit var dateTimeFormat: String

    init {
        val formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        if (formatter is SimpleDateFormat) {
            // Java: yy -> YYYY, dd -> DD
            dateFormat = formatter.toPattern().replace('d', 'D').replace("y", "YY")
        }
    }

    @Suppress("unused")
    fun setTimestampPrecision(precision: TimeStampPrecision) {
        dateTimeFormat = when (precision) {
            TimeStampPrecision.DAY -> dateFormat
            TimeStampPrecision.MINUTE -> "$dateFormat hh:mm"
            TimeStampPrecision.SECOND -> "$dateFormat hh:mm:ss"
        }
    }
}
