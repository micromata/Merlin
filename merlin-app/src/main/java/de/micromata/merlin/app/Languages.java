package de.micromata.merlin.app;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class Languages {
    public static Locale asLocale(String language) {
        return asLocale(language, false);
    }

    public static Locale asLocale(String language, boolean rootAsDefault) {
        Locale locale = StringUtils.isNotBlank(language) ? Locale.forLanguageTag(language) : null;
        return (locale != null || !rootAsDefault) ? locale : Locale.ROOT;
    }

    public static String asString(Locale locale) {
        return locale != null ? locale.getLanguage() : null;
    }
}
