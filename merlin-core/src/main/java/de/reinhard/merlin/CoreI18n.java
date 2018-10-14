package de.reinhard.merlin;

import java.util.Locale;

/**
 * For internationalization.
 */
public class CoreI18n extends I18n {
    private static CoreI18n defaultInstance = new CoreI18n();
    public static final String BUNDLE_NAME = "MerlinCoreMessagesBundle";

    public static CoreI18n getDefault() {
        return defaultInstance;
    }

    /**
     * Use this if only one locale is used (in a none multi user system).
     * At default the default message bundle "MessagesBundle" of the class path with the system's default locale is used.
     *
     * @param instance
     */
    public static void setDefault(CoreI18n instance) {
        defaultInstance = instance;
    }

    /**
     * Use this if only one locale is used (in a none multi user system).
     * Uses bundle "MessagesBundle" of the class path with the given locale.
     *
     * @param locale
     * @return new default instance for chaining.
     */
    public static CoreI18n setDefault(Locale locale) {
        defaultInstance = new CoreI18n(locale);
        return defaultInstance;
    }

    /**
     * Uses the default message bundle "MessagesBundle" of class path with systems default locale.
     */
    public CoreI18n() {
        super(BUNDLE_NAME);
    }

    public CoreI18n(Locale locale) {
        super(BUNDLE_NAME, locale);
    }
}
