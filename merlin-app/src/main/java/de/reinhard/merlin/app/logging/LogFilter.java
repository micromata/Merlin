package de.reinhard.merlin.app.logging;

/**
 * For filtering log messages.
 */
public class LogFilter {
    private String search;
    private String searchMessage;
    private String searchLoggerName;
    private String searchJavaClass;
    private LogLevel threshold;
    private Integer maxSize;

    /**
     *
     * @return Search string for all fields.
     */
    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSearchMessage() {
        return searchMessage;
    }

    public void setSearchMessage(String searchMessage) {
        this.searchMessage = searchMessage;
    }

    public String getSearchLoggerName() {
        return searchLoggerName;
    }

    public void setSearchLoggerName(String searchLoggerName) {
        this.searchLoggerName = searchLoggerName;
    }

    public String getSearchJavaClass() {
        return searchJavaClass;
    }

    public void setSearchJavaClass(String searchJavaClass) {
        this.searchJavaClass = searchJavaClass;
    }

    public LogLevel getThreshold() {
        return threshold;
    }

    public void setThreshold(LogLevel threshold) {
        this.threshold = threshold;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }
}
