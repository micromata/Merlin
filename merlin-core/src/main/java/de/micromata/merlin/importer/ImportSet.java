package de.micromata.merlin.importer;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This holds a list of {@link ImportDataEntry}, upload e. g. by one sheet (e. g. Excel sheet) or csv file.
 */
public class ImportSet<T> {
    public enum Status {
        NOT_RECONCILED("not_reconciled"), RECONCILED("reconciled"), HAS_ERRORS("has_errors"),
        IMPORTED("imported"), NOTHING_TODO("nothing_to_do");

        private String key;

        /**
         * @return The key may be used e. g. for i18n.
         */
        public String getKey() {
            return key;
        }

        Status(String key) {
            this.key = key;
        }

        public boolean isIn(Status... status) {
            for (Status st : status) {
                if (this == st) {
                    return true;
                }
            }
            return false;
        }
    }

    private int counter = -1;
    @Getter
    private List<ImportDataEntry<T>> dataEntries = new ArrayList<>();
    @Getter
    private Status status = Status.NOT_RECONCILED;
    private ImportStatistics statistics = new ImportStatistics();
    private Map<Object, ImportDataEntry<T>> entryMapByIndex = new HashMap<>();
    private Map<Object, ImportDataEntry<T>> entryMapByPrimaryKey = new HashMap<>();
    /**
     * Is true, if no reconciliation is done or any modification is done after the last
     * reconciliation.
     */
    private boolean dirty = true;

    /**
     * Adds a new {@link ImportDataEntry} with the given value to the list.
     *
     * @param entry The entry to add.
     * @return this for chaining.
     */
    public ImportSet<T> add(T entry) {
        return add(entry, null);
    }

    /**
     * Adds a new {@link ImportDataEntry} with the given value to the list.
     *
     * @param entry The entry to add.
     * @param primaryKey If given, this value will be used as primary key. If null, a auto-increment sequence is used.
     * @return this for chaining.
     */
    public ImportSet<T> add(T entry, Object primaryKey) {
        dirty = true;
        ImportDataEntry<T> importEntry = new ImportDataEntry<>();
        importEntry.setValue(entry).setIndex(++counter);
        dataEntries.add(importEntry);
        entryMapByIndex.put(importEntry.getIndex(), importEntry);
        if (primaryKey != null) {
            ImportDataEntry<T> existingEntry = entryMapByPrimaryKey.get(primaryKey);
            if (existingEntry != null) {
                existingEntry.addError("merlin.importer.validation_error.primary_key_not_unique", primaryKey);
                importEntry.addError("merlin.importer.validation_error.primary_key_not_unique", primaryKey);
            } else {
                entryMapByPrimaryKey.put(primaryKey, importEntry);
            }
        }
        return this;
    }

    public ImportStatistics getStatistics() {
        if (dirty == false) {
            return statistics;
        }
        statistics.reset(dataEntries.size());
        for (ImportDataEntry<T> entry : dataEntries) {
            if (entry.getStatus() == null) {
                statistics.incrementNumberOfNotReconciledElements();
            } else
            switch (entry.getStatus()) {
                case FAULTY:
                    statistics.incrementNumberOfFaultyElements();
                    statistics.incrementNumberOfUncommittedElements();
                    statistics.incrementNumberOfNotReconciledElements();
                    break;
                case NEW:
                    statistics.incrementNumberOfNewElements();
                    break;
                case MODIFIED:
                    statistics.incrementNumberOfModifiedElements();
                    break;
                case UNMODIFIED:
                    statistics.incrementNumberOfUnmodifiedElements();
                    break;
                case COMMITTED:
                    statistics.incrementNumberOfCommittedElements();
                    break;
            }
        }
        return statistics;
    }

    public ImportStatistics reconcile() {
        for (ImportDataEntry<T> entry : this.dataEntries) {
            if (entry.getStatus() == ImportDataEntry.Status.FAULTY) {
                continue;
            }
            T oldEntry = getAlreadyPersistedEntry(entry);
            if (oldEntry != null) {
                entry.setOldValue(oldEntry);
                reconcile(entry);
            } else {
                entry.setStatus(ImportDataEntry.Status.NEW);
            }
        }
        dirty =  true;
        return getStatistics();
    }


    /**
     * You should override this method for enabling this functionality.
     * <br>
     * Compares {@link ImportDataEntry#oldValue} with {@link ImportDataEntry#value}. Will only be called, if {@link ImportDataEntry#oldValue} is given.
     * @param entry The entry to reconcile.
     */
    protected void reconcile(ImportDataEntry<T> entry) {
    }

    /**
     * Override this method for supporting reconciling of new entries to import against any previous persisted entry.
     *
     * @param entry The new entry to import.
     * @return The already persisted entry (if exist) with the same index / primary key (e. g. in the data base).
     */
    public T getAlreadyPersistedEntry(ImportDataEntry<T> entry) {
        return null;
    }
}
