package de.reinhard.merlin.app.logging;

/**
 * For filtering log messages.
 */
public class LogFilter {
    private String search;
    private LogLevel threshold;
    private Integer maxSize;
    private boolean ascendingOrder;
    private boolean showStackTraces;
    private Integer lastReceivedLogOrderNumber;

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

    public void setAscendingOrder(boolean ascendingOrder) {
        this.ascendingOrder = ascendingOrder;
    }

    /**
     *
     * @return false at default (default is descending order of the result).
     */
    public boolean isAscendingOrder() {
        return ascendingOrder;
    }


    public boolean isShowStackTraces() {
        return showStackTraces;
    }

    public void setShowStackTraces(boolean showStackTraces) {
        this.showStackTraces = showStackTraces;
    }

    /**
     *
     * @return If given, all log entries with order orderNumber higher than this orderNumber will be queried.
     */
    public Integer getLastReceivedLogOrderNumber() {
        return lastReceivedLogOrderNumber;
    }

    public void setLastReceivedLogOrderNumber(Integer lastReceivedLogOrderNumber) {
        this.lastReceivedLogOrderNumber = lastReceivedLogOrderNumber;
    }
}
