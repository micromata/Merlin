package de.micromata.merlin.importer;

import de.micromata.merlin.ResultMessage;
import de.micromata.merlin.ResultMessageStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds one single data entry (e. g. one row of an Excel or CSV-file.
 *
 */
public class ImportDataEntry<T> {
    enum Status {NEW, MODIFIED, UNMODIFIED, COMMITTED, FAULTY}

    private List<ResultMessage> errorMessages;

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
