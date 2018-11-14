package de.micromata.merlin.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class I18nLogEntryTest {
    @Test
    void logTest() {
        check("i18n='merlin.log.hello'", "merlin.log.hello");
        check("i18n='merlin.log.hello', args=['hurzel']", "merlin.log.hello", "hurzel");
        check("i18n='merlin.log.hello', args=['null', '', '']", "merlin.log.hello", "null", null, "");
    }

    private void check(String expectedString, String i18nKey, String... args) {
        I18nLogEntry entry1 = new I18nLogEntry(i18nKey, (Object[])args);
        String str = entry1.toString();
        assertEquals(prepare(expectedString), str);
        I18nLogEntry entry2 = I18nLogEntry.parse(str);
        check(entry1, entry2);
    }

    private void check(I18nLogEntry entry1, I18nLogEntry entry2) {
        assertEquals(entry1.getI18nKey(), entry2.getI18nKey());
        if (entry1.getArgs() == null) {
            assertNull(entry2.getArgs());
        } else {
            assertNotNull(entry2.getArgs());
            assertTrue(entry1.getArgs().length == entry2.getArgs().length);
            for (int i = 0; i < entry1.getArgs().length; i++) {
                if (entry1.getArgs()[i] == null)
                    assertEquals("", entry2.getArgs()[i]);
                else
                    assertEquals(entry1.getArgs()[i], entry2.getArgs()[i]);
            }
        }

    }

    private String prepare(String str) {
        return str.replace('\'', '"');
    }
}
