package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class TranslationEntry {
    @Getter
    @JsonProperty
    private String i18nKey;
    /**
     * Key is lang, value is the translation string.
     */
    @JsonProperty
    private Map<String, String> values = new HashMap<>();

    /**
     * For deserialization.
     */
    TranslationEntry() {
    }

    public TranslationEntry(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String getTranslation(String lang) {
        return values.get(lang);
    }

    public void putTranslation(String lang, String value) {
        if (StringUtils.isEmpty(value) && StringUtils.isNotEmpty(getTranslation(lang))) {
            // Don't overwrite existing values with empty values.
            return;
        }
        values.put(lang, value);
    }
}
