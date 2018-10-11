package de.reinhard.merlin.app.logging;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;

public class Log4jMemoryAppender extends AppenderSkeleton {
    private static final int MAX_RESULT_SIZE = 1000;
    private static final int QUEUE_SIZE = 10000;
    private static Log4jMemoryAppender instance;

    private int entryCounter = 0;

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

    CircularFifoQueue<LoggingEventData> queue = new CircularFifoQueue(QUEUE_SIZE);

    @Override
    protected void append(LoggingEvent event) {
        LoggingEventData eventData = new LoggingEventData(event);
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

    public List<LoggingEventData> query(LogFilter filter) {
        List<LoggingEventData> result = new ArrayList<>();
        if (filter == null) {
            return result;
        }
        int maxSize = filter.getMaxSize() != null ? filter.getMaxSize() : MAX_RESULT_SIZE;
        if (maxSize > MAX_RESULT_SIZE) {
            maxSize = MAX_RESULT_SIZE;
        }
        int counter = 0;
        for (LoggingEventData event : queue) {
            if (!event.getLevel().matches(filter.getThreshold())) {
                continue;
            }
            String str = StringUtils.join(event.getLoggerName(), event.getJavaClass(), event.getMessage(), event.getLevel(), event.getLogDate(), "|#|");
            if (StringUtils.isEmpty(filter.getSearch()) || matches(str, filter.getSearch())) {
                if (filter.isAscendingOrder()) {
                    result.add(event);
                } else {
                    result.add(0, event);
                }
                event.number = entryCounter++;
                if (counter++ > maxSize) {
                    break;
                }
            }
        }
        return result;
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
