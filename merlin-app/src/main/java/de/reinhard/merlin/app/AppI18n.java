package de.reinhard.merlin.app;

import de.reinhard.merlin.I18n;

import java.util.Locale;

/**
 * For internationalization.
 */
public class AppI18n extends I18n {
    private static AppI18n defaultInstance = new AppI18n();
    public static final String BUNDLE_NAME = "MerlinAppMessagesBundle";

    public static AppI18n getDefault() {
        return defaultInstance;
    }

    /**
     * Use this if only one locale is used (in a none multi user system).
     * At default the default message bundle "MessagesBundle" of the class path with the system's default locale is used.
     *
     * @param instance
     */
    public static void setDefault(AppI18n instance) {
        defaultInstance = instance;
    }

    /**
     * Use this if only one locale is used (in a none multi user system).
     * Uses bundle "MessagesBundle" of the class path with the given locale.
     *
     * @param locale
     * @return new default instance for chaining.
     */
    public static AppI18n setDefault(Locale locale) {
        defaultInstance = new AppI18n(locale);
        return defaultInstance;
    }

    /**
     * Uses the default message bundle "MessagesBundle" of class path with systems default locale.
     */
    public AppI18n() {
        super(BUNDLE_NAME);
    }

    public AppI18n(Locale locale) {
        super(BUNDLE_NAME, locale);
    }
}
