package de.micromata.merlin.server.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public enum LogLevel {
    ERROR, WARN, INFO, DEBUG, TRACE;

    /**
     * @param treshold
     * @return True, if this log level is equals or higher than given treshold. ERROR is the highest and TRACE the lowest.
     */
    public boolean matches(LogLevel treshold) {
        if (treshold == null) {
            return true;
        }
        return this.ordinal() <= treshold.ordinal();
    }

    public static LogLevel getLevel(LoggingEvent event) {
        switch (event.getLevel().toInt()) {
            case Level.ERROR_INT:
                return LogLevel.ERROR;
            case Level.INFO_INT:
                return LogLevel.INFO;
            case Level.DEBUG_INT:
                return LogLevel.DEBUG;
            case Level.WARN_INT:
                return LogLevel.WARN;
            case Level.TRACE_INT:
                return LogLevel.TRACE;
            default:
                return LogLevel.ERROR;
        }

    }

    public static String getSupportedValues() {
        return StringUtils.join(LogLevel.values(), ", ");
    }
}
