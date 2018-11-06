package de.micromata.merlin.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class I18nClientMessagesTest {
    @Test
    public void prepareForClientTest() throws Exception {
        assertEquals("", I18nClientMessages.prepareForReactClient(""));
        assertEquals("Test", I18nClientMessages.prepareForReactClient("Test"));
        assertEquals("{0}", I18nClientMessages.prepareForReactClient("{0}"));
        assertEquals("{0}", I18nClientMessages.prepareForReactClient("'{0}'"));
        assertEquals("'{0}'", I18nClientMessages.prepareForReactClient("''{0}''"));
        assertEquals("{test}", I18nClientMessages.prepareForReactClient("'{test}'"));
        assertEquals("In sheet '{0}', column {1}:'{2}' and row #{3}: {{4}}",
                I18nClientMessages.prepareForReactClient("In sheet ''{0}'', column {1}:''{2}'' and row #{3}: '{'{4}'}'"));
    }
}