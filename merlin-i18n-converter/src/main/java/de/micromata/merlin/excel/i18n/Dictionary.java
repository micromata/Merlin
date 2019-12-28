package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Dictionary {
    /**
     * key is the i18n-key.
     */
    @JsonProperty
    private Map<String, TranslationEntry> translations = new HashMap<>();
    private Set<String> keys = new TreeSet<>();
    private Set<String> usedLangs = new TreeSet<>();
    private StringBuilder logging = new StringBuilder();
    /**
     * Key is lang and value contains all differing translations.
     */
    @JsonIgnore
    private Map<String, SortedSet<TranslationDiffEntry>> diffEntryMap = new HashMap<>();
    @JsonIgnore
    private Dictionary diffDictionary;

    /**
     * If true, then new keys will be added (default). If false, only translations to existing keys will be added.
     * Translations for not existing keys will be ignored on import.
     */
    @JsonIgnore
    private boolean createKeyIfNotPresent = true;

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

    public boolean isModified(String lang, String key) {
        if (diffDictionary == null || key == null) {
            return false;
        }
        SortedSet<TranslationDiffEntry> result = getDifferences(lang);
        for (TranslationDiffEntry entry : result) {
            if (key.equals(entry.getI18nKey())) {
                return true;
            }
        }
        return false;
    }

    public SortedSet<TranslationDiffEntry> getDifferences(String lang) {
        SortedSet<TranslationDiffEntry> result = diffEntryMap.get(lang);
        if (result != null) {
            return result;
        }
        result = new TreeSet<>();
        diffEntryMap.put(lang, result);
        if (diffDictionary == null) {
            return result;
        }
        for (TranslationEntry entry : translations.values()) {
            checkDiff(result, entry.getI18nKey(), entry.getTranslation(lang),
                    diffDictionary.getTranslation(lang, entry.getI18nKey()));
        }
        for (TranslationEntry otherEntry : diffDictionary.translations.values()) {
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

    public Set<String> getKeys() {
        return this.keys;
    }

    public Set<String> getUsedLangs() {
        return this.usedLangs;
    }

    public Dictionary getDiffDictionary() {
        return this.diffDictionary;
    }

    public boolean isCreateKeyIfNotPresent() {
        return this.createKeyIfNotPresent;
    }

    public boolean isOverwriteExistingTranslations() {
        return this.overwriteExistingTranslations;
    }

    public void setDiffDictionary(Dictionary diffDictionary) {
        this.diffDictionary = diffDictionary;
    }

    public void setCreateKeyIfNotPresent(boolean createKeyIfNotPresent) {
        this.createKeyIfNotPresent = createKeyIfNotPresent;
    }

    public void setOverwriteExistingTranslations(boolean overwriteExistingTranslations) {
        this.overwriteExistingTranslations = overwriteExistingTranslations;
    }
}
