package de.micromata.merlin.excel.i18n;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains an entry of current dictionary which differs to diffDictionary.
 */
public class TranslationDiffEntry implements Comparable<TranslationDiffEntry> {
    @Getter
    private String i18nKey;
    @Getter
    private String thisValue;
    @Getter
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
}
