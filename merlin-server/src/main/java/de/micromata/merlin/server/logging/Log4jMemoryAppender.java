package de.micromata.merlin.server.logging;

import de.micromata.merlin.CoreI18n;
import de.micromata.merlin.I18n;
import de.micromata.merlin.utils.I18nLogEntry;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Log4jMemoryAppender extends AppenderSkeleton {
    private static final int MAX_RESULT_SIZE = 1000;
    private static final int QUEUE_SIZE = 10000;
    private static Log4jMemoryAppender instance;

    private int lastLogEntryOrderNumber = -1;

    public static Log4jMemoryAppender getInstance() {
        return instance;
    }

    public Log4jMemoryAppender() {
        if (instance != null) {
            throw new IllegalArgumentException("Log4jMemoryAppender shouldn't be instantiated twice!");
        }
        instance = this;
    }

    /**
     * For test purposes.
     */
    Log4jMemoryAppender(boolean ignoreMultipleInstance) {

    }

    CircularFifoQueue<LoggingEventData> queue = new CircularFifoQueue<>(QUEUE_SIZE);

    @Override
    protected void append(LoggingEvent event) {
        LoggingEventData eventData = new LoggingEventData(event);
        eventData.orderNumber = ++lastLogEntryOrderNumber;
        queue.add(eventData);
    }

    /**
     * For testing purposes.
     *
     * @param event
     */
    void append(LoggingEventData event) {
        queue.add(event);
    }

    public List<LoggingEventData> query(LogFilter filter, Locale locale) {
        List<LoggingEventData> result = new ArrayList<>();
        if (filter == null) {
            return result;
        }
        int maxSize = filter.getMaxSize() != null ? filter.getMaxSize() : MAX_RESULT_SIZE;
        if (maxSize > MAX_RESULT_SIZE) {
            maxSize = MAX_RESULT_SIZE;
        }
        int counter = 0;
        boolean hasMdcFilters = filter.hasMdcFilters();
        I18n i18n = CoreI18n.getDefault().get(locale);
        for (LoggingEventData event : queue) {
            if (!event.getLevel().matches(filter.getThreshold())) {
                continue;
            }
            if (filter.getLastReceivedLogOrderNumber() != null) {
                if (event.getOrderNumber() <= filter.getLastReceivedLogOrderNumber()) {
                    continue;
                }
            }
            if (hasMdcFilters) {
                if (!event.matchesAtLeastOneMdcValue(filter))
                    // log event doesn't match any mdc value.
                    continue;
            }
            String logString = null;
            String message = event.getMessage();
            boolean localizedMessage = false;
            if (message != null && message.startsWith("i18n=")) {
                I18nLogEntry i18nLogEntry = I18nLogEntry.parse(message);
                message = i18n.formatMessage(i18nLogEntry.getI18nKey(), i18nLogEntry.getArgs());
                localizedMessage = true;
            }

            if (StringUtils.isNotBlank(filter.getSearch())) {
                StringBuilder sb = new StringBuilder();
                sb.append(event.getLogDate());
                append(sb, event.getLevel(), true);
                append(sb, message, true);
                append(sb, event.getJavaClass(), true);
                append(sb, event.getStackTrace(), filter.isShowStackTraces());
                logString = sb.toString();
            }
            if (logString == null || matches(logString, filter.getSearch())) {
                LoggingEventData resultEvent = event;
                if (localizedMessage) {
                    // Need a clone
                    resultEvent = (LoggingEventData)event.clone();
                    resultEvent.setMessage(message);
                }
                if (filter.isAscendingOrder()) {
                    result.add(resultEvent);
                } else {
                    result.add(0, resultEvent);
                }
                if (counter++ > maxSize) {
                    break;
                }
            }
        }
        return result;
    }

    private void append(StringBuilder sb, Object value, boolean append) {
        if (!append || value == null) {
            return;
        }
        sb.append("|#|").append(value);
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }

    private boolean matches(String str, String searchString) {
        if (StringUtils.isBlank(str)) {
            return StringUtils.isBlank(searchString);
        }
        if (StringUtils.isBlank(searchString)) {
            return true;
        }
        return str.toLowerCase().contains(searchString.toLowerCase());
    }
}
