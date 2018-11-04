package de.micromata.merlin.app;

import de.reinhard.merlin.I18n;

import java.util.Locale;

/**
 * For internationalization.
 */
public class AppI18n extends I18n {
    public static final String BUNDLE_NAME = "MerlinAppMessagesBundle";

    static {
        Locale.setDefault(Locale.ENGLISH);
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
