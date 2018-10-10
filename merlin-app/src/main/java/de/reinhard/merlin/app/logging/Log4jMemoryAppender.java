package de.reinhard.merlin.app.logging;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;

public class Log4jMemoryAppender extends AppenderSkeleton {
    private static final int MAX_RESULT_SIZE = 1000;
    private static Log4jMemoryAppender instance;

    public static Log4jMemoryAppender getInstance() {
        return instance;
    }

    public Log4jMemoryAppender() {
        if (instance != null) {
            throw new IllegalArgumentException("Log4jMemoryAppender shouldn't be instantiated twice!");
        }
        instance = this;
    }

    CircularFifoQueue<LoggingEventData> queue = new CircularFifoQueue(10000);

    @Override
    protected void append(LoggingEvent event) {
        LoggingEventData eventData = new LoggingEventData(event);
        queue.add(eventData);
    }

    public List<LoggingEventData> query(LogFilter filter) {
        List<LoggingEventData> result = new ArrayList<>();
        if (filter == null) {
            return result;
        }
        int counter = 0;
        int maxNumber = filter.getMaxSize() != null ? filter.getMaxSize() : MAX_RESULT_SIZE;
        if (maxNumber > MAX_RESULT_SIZE) {
            maxNumber = MAX_RESULT_SIZE;
        }
        for (LoggingEventData event : queue) {
            if (counter++ > maxNumber) {
                break;
            }
            if (!event.getLevel().matches(filter.getThreshold())) {
                continue;
            }
            if (matches(filter, event.getLoggerName(), filter.getSearchLoggerName()) ||
                    matches(filter, event.getMessage(), filter.getSearchMessage()) ||
                    matches(filter, event.getJavaClass(), filter.getSearchJavaClass())) {
                result.add(event);
            }
        }
        return result;
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }

    private boolean matches(LogFilter filter, String str, String searchString) {
        if (matches(str, searchString)) {
            return true;
        }
        return matches(str, filter.getSearch());
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
