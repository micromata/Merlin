package de.reinhard.merlin.app.logging;

import de.reinhard.merlin.logging.MDCKey;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * For easier serialization: JSON
 */
public class LoggingEventData {
    private SimpleDateFormat ISO_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int orderNumber;
    LogLevel level;
    String message;
    private String messageObjectClass;
    private String loggerName;
    private String logDate;
    String javaClass;
    private String javaClassSimpleName;
    private String lineNumber;
    private String methodName;
    private String stackTrace;
    private String mdcTemplatePK;
    private String mdcTemplateDefinitionPk;

    LoggingEventData() {

    }

    public LoggingEventData(LoggingEvent event) {
        level = LogLevel.getLevel(event);
        message = event.getRenderedMessage();
        messageObjectClass = event.getMessage().getClass().toString();
        loggerName = event.getLoggerName();
        logDate = getIsoLogDate(event.timeStamp);
        LocationInfo info = event.getLocationInformation();
        Throwable throwable = event.getThrowableInformation() != null ? event.getThrowableInformation().getThrowable() : null;
        if (throwable != null) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            stackTrace = writer.toString();
        }
        if (info != null) {
            javaClass = info.getClassName();
            javaClassSimpleName = ClassUtils.getShortClassName(info.getClassName());
            lineNumber = info.getLineNumber();
            methodName = info.getMethodName();
        }
        mdcTemplatePK = getMDC(event, MDCKey.TEMPLATE_PK);
        mdcTemplateDefinitionPk = getMDC(event, MDCKey.TEMPLATE_DEFINITION_PK);
    }

    private String getMDC(LoggingEvent event, MDCKey type) {
        Object value = event.getMDC(type.mdcKey());
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
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

    public String getLogDate() {
        return logDate;
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

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public String getMdcTemplatePK() {
        return mdcTemplatePK;
    }

    public String getMdcTemplateDefinitionPk() {
        return mdcTemplateDefinitionPk;
    }

    public boolean matchesAtLeastOneMdcValue(LogFilter filter) {
        if (mdcTemplatePK == null && mdcTemplateDefinitionPk == null) {
            return false;
        }
        if (StringUtils.isNotEmpty(filter.getMdcTemplatePrimaryKey())) {
            if (StringUtils.equalsIgnoreCase(mdcTemplatePK, filter.getMdcTemplatePrimaryKey()))
                return true;
        }
        if (StringUtils.isNotEmpty(filter.getMdcTemplateDefinitionPrimaryKey())) {
            if (StringUtils.equalsIgnoreCase(mdcTemplateDefinitionPk, filter.getMdcTemplateDefinitionPrimaryKey()))
                return true;
        }
        return false;
    }

    private String getIsoLogDate(long millis) {
        synchronized (ISO_DATEFORMAT) {
            return ISO_DATEFORMAT.format(new Date(millis));
        }
    }
}
