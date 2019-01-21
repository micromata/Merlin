package de.micromata.merlin.importer;

import lombok.Getter;

/**
 * This holds statistics of (imported) data entries.
 */
public class ImportStatistics {
    @Getter
    private int totalNumberOfElements;

    /**
     * Number of elements not yet reconciled (against already stored / persisted entries).
     */
    @Getter
    private int numberOfNotReconciledElements;

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
     * Number of saved / committed elements (e. g. to the data base).
     */
    @Getter
    private int numberOfCommittedElements;

    /**
     * Number of unsaved / uncommitted elements (e. g. to the data base).
     */
    @Getter
    private int numberOfUncommittedElements;

    public ImportStatistics() {
        reset(0);
    }

    /**
     * @param totalNumberOfElements The total number of elements to set.
     * @return this for chaining.
     */
    public ImportStatistics reset(int totalNumberOfElements) {
        this.totalNumberOfElements = totalNumberOfElements;
        this.numberOfNotReconciledElements = 0;
        this.numberOfFaultyElements = 0;
        this.numberOfCommittedElements = 0;
        this.numberOfModifiedElements = 0;
        this.numberOfUnmodifiedElements = 0;
        this.numberOfNewElements = 0;
        return this;
    }

    void incrementNumberOfNotReconciledElements() {
        ++this.numberOfNotReconciledElements;
    }

    void incrementNumberOfNewElements() {
        ++this.numberOfNewElements;
    }

    void incrementNumberOfModifiedElements() {
        ++this.numberOfModifiedElements;
    }

    void incrementNumberOfUnmodifiedElements() {
        ++this.numberOfUnmodifiedElements;
    }

    void incrementNumberOfFaultyElements() {
        ++this.numberOfFaultyElements;
    }

    void incrementNumberOfCommittedElements() {
        ++this.numberOfCommittedElements;
    }

    void incrementNumberOfUncommittedElements() {
        ++this.numberOfUncommittedElements;
    }
}
