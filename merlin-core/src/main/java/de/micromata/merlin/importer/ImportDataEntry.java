package de.micromata.merlin.importer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Holds one single data entry (e. g. one row of an Excel or CSV-file.
 *
 * @param <T>
 */
public class ImportDataEntry<T> {
    /**
     * If your data entries has already a primary key, such as PIN etc., you should use this. Otherwise
     */
    @Getter
    @Setter
    protected Object primaryKey;
    /**
     * The index will be automatically set by ImportSet. If no other primary key is given, this index is used for
     * comparing entries with data base entries.
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    protected int index;
    /**
     * The value to import.
     */
    @Getter
    @Setter
    protected T value;
    /**
     * If a value was already imported e. g. in the data base before this current import, the old value should represent
     * the data base version of this object. Is null for the first import or if there was now old value found with the
     * same index or primary key.
     */
    @Getter
    @Setter
    protected T oldValue;
    /**
     * If true, this data entry is selected for updating.
     */
    @Getter
    @Setter
    protected boolean selected;

    /**
     * If true, the primary key is already used by another data entry.
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    protected boolean uniqueConstraintViolation;
}
