package de.reinhard.merlin.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppI18nTest {
    @Test
    public void prepareForClientTest() throws Exception {
        assertEquals("", AppI18n.prepareForReactClient(""));
        assertEquals("Test", AppI18n.prepareForReactClient("Test"));
        assertEquals("{0}", AppI18n.prepareForReactClient("{0}"));
        assertEquals("{0}", AppI18n.prepareForReactClient("'{0}'"));
        assertEquals("'{0}'", AppI18n.prepareForReactClient("''{0}''"));
        assertEquals("{test}", AppI18n.prepareForReactClient("'{test}'"));
        assertEquals("In sheet '{0}', column {1}:'{2}' and row #{3}: {{4}}",
                AppI18n.prepareForReactClient("In sheet ''{0}'', column {1}:''{2}'' and row #{3}: '{'{4}'}'"));
    }
}