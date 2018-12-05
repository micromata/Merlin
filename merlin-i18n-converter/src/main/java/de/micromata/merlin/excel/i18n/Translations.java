package de.micromata.merlin.excel.i18n;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Translations {
    private Map<String, TranslationEntry> translations = new HashMap<>();
    @Getter
    private Set<String> keys = new TreeSet<>();
    @Getter
    @Setter
    private String i18nKey;
    @Getter
    private Set<String> usedLangs = new TreeSet<>();

    public void addTranslation(String lang, String key, String translation) {
        usedLangs.add(lang);
        if (!keys.contains(key))
            keys.add(key);
        TranslationEntry entry = translations.get(key);
        if (entry == null) {
            entry = new TranslationEntry(key);
            translations.put(key, entry);
        }
        entry.putTranslation(lang, translation);
    }

    public String getTranslation(String lang, String key) {
        TranslationEntry entry = getEntry(key);
        return entry != null ? entry.getTranslation(lang) : null;
    }

    public TranslationEntry getEntry(String key) {
        return translations.get(key);
    }
}
