package de.micromata.merlin.importer;

/**
 * This holds statistics of (imported) data entries.
 */
public class ImportStatistics {
    private int totalNumberOfElements;

    /**
     * Number of elements not yet reconciled (against already stored / persisted entries).
     */
    private int numberOfNotReconciledElements;

    /**
     * Number of elements not already stored / not found in the data base.
     */
    private int numberOfNewElements;

    /**
     * Number of elements already stored / found in the data base but modified.
     */
    private int numberOfModifiedElements;

    /**
     * Number of elements already stored / found in the data base but not modified.
     */
    private int numberOfUnmodifiedElements;

    /**
     * Number of elements with errors.
     */
    private int numberOfFaultyElements;

    /**
     * Number of saved / committed elements (e. g. to the data base).
     */
    private int numberOfCommittedElements;

    /**
     * Number of unsaved / uncommitted elements (e. g. to the data base).
     */
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

    public int getTotalNumberOfElements() {
        return this.totalNumberOfElements;
    }

    public int getNumberOfNotReconciledElements() {
        return this.numberOfNotReconciledElements;
    }

    public int getNumberOfNewElements() {
        return this.numberOfNewElements;
    }

    public int getNumberOfModifiedElements() {
        return this.numberOfModifiedElements;
    }

    public int getNumberOfUnmodifiedElements() {
        return this.numberOfUnmodifiedElements;
    }

    public int getNumberOfFaultyElements() {
        return this.numberOfFaultyElements;
    }

    public int getNumberOfCommittedElements() {
        return this.numberOfCommittedElements;
    }

    public int getNumberOfUncommittedElements() {
        return this.numberOfUncommittedElements;
    }
}
