package de.micromata.merlin;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreI18nTest {
    @Test
    void testResourceBundles() {
        Set<String> translations = CoreI18n.getAllTranslations("merlin.word.templating.sheet.configuration.name");
        assertEquals(2, translations.size());
        assertTrue(translations.contains("Konfiguration"));
        assertTrue(translations.contains("Configuration"));
    }
}
