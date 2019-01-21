package de.micromata.merlin.utils;

public class I18nLogger {
    /**
     * Creates a message with i18n key and optional args, which are parseable for a localized output.
     *
     * @param i18nkey The i18n key.
     * @param args    The args (params) for the log entry.
     * @return The log entry as String.
     * @see I18nLogEntry
     */
    public static String get(String i18nkey, Object... args) {
        I18nLogEntry entry = new I18nLogEntry(i18nkey, args);
        return entry.toString();
    }
}
