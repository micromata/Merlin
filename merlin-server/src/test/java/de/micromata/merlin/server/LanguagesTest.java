package de.micromata.merlin.server;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LanguagesTest {
    @Test
    public void asStringTest() throws Exception {
        assertEquals("de", Languages.asString(Locale.GERMAN));
        assertEquals("en", Languages.asString(Locale.ENGLISH));
    }

    @Test
    public void asLocaleTest() throws Exception {
        assertEquals("de", Languages.asLocale("de").getLanguage());
        assertEquals("en", Languages.asLocale("en").getLanguage());
        assertNull(Languages.asLocale(null));
        assertNotNull(Languages.asLocale(null, true));
        assertEquals(Locale.ROOT, Languages.asLocale(null, true));
    }
}
