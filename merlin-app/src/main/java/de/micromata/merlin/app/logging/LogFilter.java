package de.micromata.merlin.app.logging;

import org.apache.commons.lang3.StringUtils;

import java.beans.Transient;

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
    private String mdcTemplatePrimaryKey;
    private String mdcTemplateDefinitionPrimaryKey;

    /**
     * @return Search string for all fields.
     */
    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getMdcTemplatePrimaryKey() {
        return mdcTemplatePrimaryKey;
    }

    /**
     * If given, only messages containing at least one of the mdc values will be filtered.
     *
     * @param mdcTemplatePrimaryKey
     */
    public void setMdcTemplatePrimaryKey(String mdcTemplatePrimaryKey) {
        this.mdcTemplatePrimaryKey = mdcTemplatePrimaryKey;
    }

    public String getMdcTemplateDefinitionPrimaryKey() {
        return mdcTemplateDefinitionPrimaryKey;
    }

    /**
     * If given, only messages containing at least one of the mdc values will be filtered.
     *
     * @param mdcTemplateDefinitionPrimaryKey
     */
    public void setMdcTemplateDefinitionPrimaryKey(String mdcTemplateDefinitionPrimaryKey) {
        this.mdcTemplateDefinitionPrimaryKey = mdcTemplateDefinitionPrimaryKey;
    }

    /**
     *
     * @return true, if at least one mdc value is given.
     */
    @Transient
    public boolean hasMdcFilters() {
        return StringUtils.isNotEmpty(mdcTemplatePrimaryKey) || StringUtils.isNotEmpty(mdcTemplateDefinitionPrimaryKey);
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
     * @return If given, all log entries with order orderNumber higher than this orderNumber will be queried.
     */
    public Integer getLastReceivedLogOrderNumber() {
        return lastReceivedLogOrderNumber;
    }

    public void setLastReceivedLogOrderNumber(Integer lastReceivedLogOrderNumber) {
        this.lastReceivedLogOrderNumber = lastReceivedLogOrderNumber;
    }
}
