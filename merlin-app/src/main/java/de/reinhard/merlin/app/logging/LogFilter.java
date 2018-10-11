package de.reinhard.merlin.app.logging;

/**
 * For filtering log messages.
 */
public class LogFilter {
    private String search;
    private LogLevel threshold;
    private Integer maxSize;
    private boolean ascendingOrder;

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
}
