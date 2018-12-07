package de.micromata.merlin.excel.i18n;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
    private StringBuilder logging = new StringBuilder();

    /**
     * If true, then new keys will be added (default). If false, only translations to existing keys will be added.
     * Translations for not existing keys will be ignored on import.
     */
    @Getter
    @Setter
    private boolean createKeyIfNotPresent = true;

    @Getter
    @Setter
    private boolean overwriteExistingTranslations = false;

    public void addTranslation(String lang, String key, String translation) {
        if (!keys.contains(key)) {
            if (!createKeyIfNotPresent) {
                // Don't add translations for non-existing keys.
                logging.append("I lang='" + lang + "', key='" + key + "'. Skipping new key (cli option -.read-merge). Translation='"
                        + translation + "'\n");
                return;
            }
            logging.append("C lang='" + lang + "', key='" + key + "'. Create new key (cli option -.read-merge). Translation='"
                    + translation + "'\n");
            keys.add(key);
        }
        usedLangs.add(lang);
        TranslationEntry entry = translations.get(key);
        if (entry == null) {
            entry = new TranslationEntry(key);
            translations.put(key, entry);
        } else if (!overwriteExistingTranslations && StringUtils.isNotBlank(entry.getTranslation(lang))) {
            // Do not overwrite existing translations.
            logging.append("I lang='" + lang + "', key='" + key + "'. Doesn't overwrite existing translation '" + entry.getTranslation(lang)
                    + "'. Ignoring new translation='"
                    + translation + "'\n");
            return;
        }
        logging.append("A lang='" + lang + "', key='" + key + "'. Adding translation '" + translation + "'\n");
        entry.putTranslation(lang, translation);
    }

    public String getTranslation(String lang, String key) {
        TranslationEntry entry = getEntry(key);
        return entry != null ? entry.getTranslation(lang) : null;
    }

    public TranslationEntry getEntry(String key) {
        return translations.get(key);
    }

    public String getLogging() {
        return logging.toString();
    }
}
