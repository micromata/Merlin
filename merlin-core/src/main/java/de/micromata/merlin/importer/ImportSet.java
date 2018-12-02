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
    private int counter = -1;
    @Getter
    private List<ImportDataEntry<T>> dataEntries = new ArrayList<>();
    @Getter
    private ImportStatus status = ImportStatus.NOT_RECONCILED;
    @Getter
    private ImportStatistics<T> statistics = new ImportStatistics<>();
    private Map<Object, ImportDataEntry<T>> entryMapByIndex = new HashMap<>();
    private Map<Object, ImportDataEntry<T>> entryMapByPrimaryKey = new HashMap<>();

    /**
     * Adds a new {@link ImportDataEntry} with the given value to the list.
     *
     * @param entry
     * @return this for chaining.
     */
    public ImportSet<T> add(T entry) {
        return add(entry, null);
    }

    /**
     * Adds a new {@link ImportDataEntry} with the given value to the list.
     *
     * @param entry
     * @param primaryKey If given, this value will be used as primary key. If null, a auto-increment sequence is used.
     * @return this for chaining.
     */
    public ImportSet<T> add(T entry, Object primaryKey) {
        ImportDataEntry<T> importEntry = new ImportDataEntry<>();
        importEntry.setValue(entry).setIndex(++counter);
        dataEntries.add(importEntry);
        entryMapByIndex.put(importEntry.getIndex(), importEntry);
        if (primaryKey != null) {
            ImportDataEntry<T> existingEntry = entryMapByIndex.get(primaryKey);
            if (existingEntry != null) {
                existingEntry.setUniqueConstraintViolation(true);
                importEntry.setUniqueConstraintViolation(true);
            } else {
                entryMapByPrimaryKey.put(primaryKey, importEntry);
            }
        }
        return this;
    }
}
