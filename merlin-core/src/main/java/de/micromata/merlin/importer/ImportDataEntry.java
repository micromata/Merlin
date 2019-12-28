package de.micromata.merlin.importer;

import de.micromata.merlin.ResultMessage;
import de.micromata.merlin.ResultMessageStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds one single data entry (e. g. one row of an Excel or CSV-file.
 *
 */
public class ImportDataEntry<T> {
    public Object getPrimaryKey() {
        return this.primaryKey;
    }

    public int getIndex() {
        return this.index;
    }

    public T getValue() {
        return this.value;
    }

    public T getOldValue() {
        return this.oldValue;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public Status getStatus() {
        return this.status;
    }

    public ImportDataEntry<T> setPrimaryKey(Object primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public ImportDataEntry<T> setIndex(int index) {
        this.index = index;
        return this;
    }

    public ImportDataEntry<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public ImportDataEntry<T> setOldValue(T oldValue) {
        this.oldValue = oldValue;
        return this;
    }

    public ImportDataEntry<T> setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public ImportDataEntry<T> setStatus(Status status) {
        this.status = status;
        return this;
    }

    enum Status {NEW, MODIFIED, UNMODIFIED, COMMITTED, FAULTY}

    private List<ResultMessage> errorMessages;

    /**
     * If your data entries has already a primary key, such as PIN etc., you should use this. Otherwise
     */
    protected Object primaryKey;
    /**
     * The index will be automatically set by ImportSet. If no other primary key is given, this index is used for
     * comparing entries with data base entries.
     */
    protected int index;
    /**
     * The value to import.
     */
    protected T value;
    /**
     * If a value was already imported e. g. in the data base before this current import, the old value should represent
     * the data base version of this object. Is null for the first import or if there was now old value found with the
     * same index or primary key.
     */
    protected T oldValue;
    /**
     * If true, this data entry is selected for updating.
     */
    protected boolean selected;

    /**
     * If true, the primary key is already used by another data entry.
     */
    protected Status status;

    /**
     * Creates a {@link ResultMessage} and add this as error message.
     * @param i18nKey Error message with the reason why this entry can't be imported as i18n key.
     * @param params Optional params for the i18n message.
     * @return this for chaining.
     */
    public ImportDataEntry<T> addError(String i18nKey, Object... params) {
        if (this.errorMessages == null) {
            this.errorMessages = new ArrayList<>();
        } else {
            // Check, if the error was already added:
            for (ResultMessage resultMessage : this.errorMessages) {
                if (i18nKey.equals(resultMessage.getMessageId())) {
                    // Was already added.
                    return this;
                }
            }
        }
        ResultMessage errorMessage = new ResultMessage(i18nKey, ResultMessageStatus.ERROR, params);
        this.errorMessages.add(errorMessage);
        this.status = Status.FAULTY;
        return this;
    }
}
