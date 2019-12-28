package de.micromata.merlin.excel.i18n;

import org.apache.commons.lang3.StringUtils;

/**
 * Contains an entry of current dictionary which differs to diffDictionary.
 */
public class TranslationDiffEntry implements Comparable<TranslationDiffEntry> {
    private String i18nKey;
    private String thisValue;
    private String otherValue;

    TranslationDiffEntry(String i18nKey, String thisValue, String otherValue) {
        this.i18nKey = i18nKey;
        this.thisValue = thisValue;
        this.otherValue = otherValue;
    }

    @Override
    public int compareTo(TranslationDiffEntry o) {
        return StringUtils.compare(i18nKey, o.i18nKey);
    }

    public String getI18nKey() {
        return this.i18nKey;
    }

    public String getThisValue() {
        return this.thisValue;
    }

    public String getOtherValue() {
        return this.otherValue;
    }
}
