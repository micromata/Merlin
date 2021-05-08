package de.micromata.merlin.excel

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Defines standard formats for dates.
 */
class Configuration {
    enum class TimeStampPrecision { DAY, MINUTE, SECOND }

    var dayFormat: String = "MM/dd/yyyy"
        set(value) {
            field = value
            dateTimeFormat = "$dayFormat hh:mm"
        }
    lateinit var dateTimeFormat: String

    init {
        val formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        if (formatter is SimpleDateFormat) {
            // Java: yy -> YYYY, dd -> DD
            dayFormat = formatter.toPattern().replace('d', 'D').replace("y", "YY")
        }
    }

    /**
     * @param dayFormat
     * @param precision DateTimeFormat will extend given dayFormat with the given precision (default is hh:mm).
     */
    @JvmOverloads
    fun setDateFormats(dayFormat: String, precision: TimeStampPrecision = TimeStampPrecision.MINUTE) {
        this.dayFormat = dayFormat
        dateTimeFormat = when (precision) {
            TimeStampPrecision.DAY -> dayFormat
            TimeStampPrecision.MINUTE -> "$dayFormat hh:mm"
            TimeStampPrecision.SECOND -> "$dayFormat hh:mm:ss"
        }
    }
}
