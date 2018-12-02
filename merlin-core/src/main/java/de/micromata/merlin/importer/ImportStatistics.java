package de.micromata.merlin.importer;

import lombok.Getter;

/**
 * This holds statistics of (imported) data entries.
 */
public class ImportStatistics<T> {
    @Getter
    private int totalNumberOfElements;

    /**
     * Number of elements not already stored / not found in the data base.
     */
    @Getter
    private int numberOfNewElements;

    /**
     * Number of elements already stored / found in the data base but modified.
     */
    @Getter
    private int numberOfModifiedElements;

    /**
     * Number of elements already stored / found in the data base but not modified.
     */
    @Getter
    private int numberOfUnmodifiedElements;

    /**
     * Number of elements with errors.
     */
    @Getter
    private int numberOfFaultyElements;

    /**
     * Is true, if no reconciliation is done or any modification is done after the last
     * reconciliation.
     */
    @Getter
    private boolean dirty = true;

    /**
     * Number of saved / commited elements (e. g. to the data base).
     */
    @Getter
    private int numberOfCommittedElements = -1;
}
