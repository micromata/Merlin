package de.micromata.merlin.server.logging;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Log4jMemoryAppenderTest {
    @Test
    public void queryTest() throws Exception {
        Log4jMemoryAppender appender = new Log4jMemoryAppender(false);
        create(appender, LogLevel.INFO, "de.merlin.Simply", "The lazy fox jumps over the river.");
        create(appender, LogLevel.DEBUG, "de.merlin.Fox","Everything fine.");
        create(appender, LogLevel.ERROR, "de.merlin.Fox","Oups, simply try everything harder.");

        testQuery(appender, null, null, "The lazy", "Everything", "Oups,");
        testQuery(appender, LogLevel.INFO, null, "The lazy", "Oups,");
        testQuery(appender, null, "every", "Everything", "Oups,");
        testQuery(appender, LogLevel.INFO, "every", "Oups,");
        testQuery(appender, LogLevel.INFO, "simply", "The lazy", "Oups,");
    }

    private void create(Log4jMemoryAppender appender, LogLevel logLevel, String className, String message) {
        LoggingEventData data = new LoggingEventData();
        data.level = logLevel;
        data.message = message;
        data.javaClass = className;
        appender.append(data);
    }

    private void testQuery(Log4jMemoryAppender appender, LogLevel treshold, String search, String... expectedMessageStarts) {
        LogFilter filter = new LogFilter();
        filter.setThreshold(treshold);
        filter.setSearch(search);
        filter.setAscendingOrder(true);
        List<LoggingEventData> result = appender.query(filter, Locale.ENGLISH);
        if (expectedMessageStarts == null) {
            assertEquals(0, result.size());
            return;
        }
        assertEquals(expectedMessageStarts.length, result.size());
        for (int i = 0; i < expectedMessageStarts.length; i++) {
            assertTrue(result.get(i).getMessage().startsWith(expectedMessageStarts[i]),
                    "Expected message not started with '" + expectedMessageStarts[i] + "': " +
                            result.get(i).getMessage());
        }
    }
}
