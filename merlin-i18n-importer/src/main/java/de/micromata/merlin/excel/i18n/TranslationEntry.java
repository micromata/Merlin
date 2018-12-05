package de.micromata.merlin.excel.i18n;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TranslationEntry {
    @Getter
    private String i18nKey;
    private Map<String, String> values = new HashMap<>();

    public TranslationEntry(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String getTranslation(String lang) {
        return values.get(lang);
    }

    public void putTranslation(String lang, String value) {
        values.put(lang, value);
    }
}
