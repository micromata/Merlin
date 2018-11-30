package de.micromata.merlin;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreI18nTest {
    @Test
    void testDefaultLocale() {
        _testDefaultLocale(Locale.GERMAN, Locale.ENGLISH, "Configuration");
        _testDefaultLocale(Locale.FRANCE, Locale.ENGLISH, "Configuration");
        _testDefaultLocale(Locale.ENGLISH, Locale.ENGLISH, "Configuration");

        _testDefaultLocale(Locale.GERMAN, Locale.FRENCH, "Konfiguration");
        _testDefaultLocale(Locale.FRANCE, Locale.FRENCH, "Configuration");
        _testDefaultLocale(Locale.ENGLISH, Locale.FRENCH, "Configuration");

        _testDefaultLocale(Locale.GERMAN, Locale.GERMAN, "Konfiguration");
        _testDefaultLocale(Locale.FRANCE, Locale.GERMAN, "Konfiguration");
        _testDefaultLocale(Locale.ENGLISH, Locale.GERMAN, "Konfiguration");

        _testDefaultLocale(Locale.GERMAN, null, "Konfiguration");
        _testDefaultLocale(Locale.FRANCE, null, "Configuration");
        _testDefaultLocale(Locale.ENGLISH, null, "Configuration");
    }

    private void _testDefaultLocale(Locale defaultLocale, Locale locale, String expected) {
        Locale.setDefault(defaultLocale);
        CoreI18n i18n = locale != null ? new CoreI18n(locale) : new CoreI18n();
        assertEquals(expected, i18n.getMessage("merlin.word.templating.sheet.configuration.name"));
    }

    @Test
    void testResourceBundles() {
        Set<String> translations = CoreI18n.getAllTranslations("merlin.word.templating.sheet.configuration.name");
        assertEquals(2, translations.size());
        assertTrue(translations.contains("Konfiguration"));
        assertTrue(translations.contains("Configuration"));
    }
}
