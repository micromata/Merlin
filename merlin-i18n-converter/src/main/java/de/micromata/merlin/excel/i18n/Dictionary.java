package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Dictionary {
    /**
     * key is the i18n-key.
     */
    @JsonProperty
    private Map<String, TranslationEntry> translations = new HashMap<>();
    @Getter
    private Set<String> keys = new TreeSet<>();
    @Getter
    private Set<String> usedLangs = new TreeSet<>();
    private StringBuilder logging = new StringBuilder();

    /**
     * If true, then new keys will be added (default). If false, only translations to existing keys will be added.
     * Translations for not existing keys will be ignored on import.
     */
    @Getter
    @Setter
    @JsonIgnore
    private boolean createKeyIfNotPresent = true;

    @Getter
    @Setter
    @JsonIgnore
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

    public void log(String log) {
        logging.append(log).append("\n");
    }


    public Dictionary() {
        logging.append("Date of generation: " + new Date() + "\n\n");
    }

    public SortedSet<TranslationDiffEntry> getDifferences(Dictionary other, String lang) {
        SortedSet<TranslationDiffEntry> result = new TreeSet<>();
        for (TranslationEntry entry : translations.values()) {
            checkDiff(result, entry.getI18nKey(), entry.getTranslation(lang),
                    other.getTranslation(lang, entry.getI18nKey()));
        }
        for (TranslationEntry otherEntry : other.translations.values()) {
            checkDiff(result, otherEntry.getI18nKey(), getTranslation(lang, otherEntry.getI18nKey()),
                    otherEntry.getTranslation(lang));
        }
        return result;
    }

    private void checkDiff(SortedSet<TranslationDiffEntry> result, String i18nKey, String thisValue, String otherValue) {
        if (StringUtils.equals(StringUtils.defaultString(thisValue), StringUtils.defaultString(otherValue)))
            return;
        result.add(new TranslationDiffEntry(i18nKey, thisValue, otherValue));
    }
}
