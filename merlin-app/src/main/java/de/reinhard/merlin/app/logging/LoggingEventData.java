package de.reinhard.merlin.app.logging;

import org.apache.commons.lang3.ClassUtils;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

/**
 * For easier serialization: JSON
 */
public class LoggingEventData {
    LogLevel level;
    String message;
    private String messageObjectClass;
    private String loggerName;
    private long timestamp;
    String javaClass;
    private String javaClassSimpleName;
    private String lineNumber;
    private String methodName;

    LoggingEventData() {

    }

    public LoggingEventData(LoggingEvent event) {
        level = LogLevel.getLevel(event);
        message = event.getRenderedMessage();
        messageObjectClass = event.getMessage().getClass().toString();
        loggerName = event.getLoggerName();
        timestamp = event.timeStamp;
        LocationInfo info = event.getLocationInformation();
        if (info != null) {
            javaClass = info.getClassName();
            javaClassSimpleName = ClassUtils.getShortClassName(info.getClassName());
            lineNumber = info.getLineNumber();
            methodName = info.getMethodName();
        }
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageObjectClass() {
        return messageObjectClass;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public String getJavaClassSimpleName() {
        return javaClassSimpleName;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public String getMethodName() {
        return methodName;
    }
}
