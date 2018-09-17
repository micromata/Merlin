package de.reinhard.merlin.app;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.prefs.Preferences;

import static de.reinhard.merlin.app.ConfigurationHandler.TEMPLATES_DIRS;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationTest {
    private final static String DIR1 = "/Users/kai/Documents/templates";
    private final static String DIR2 = "/Users/kai/Templates";
    private final static String DIR3 = "/Users/kai/Templates of \"Kai\"";
    private final static String DIR3b = "/Users/kai/Templates of \\\"Kai\\\"";

    @Test
    public void configurationSaveTest() throws Exception {
        Preferences preferences = mock(Preferences.class);
        ConfigurationHandler configurationHandler = new ConfigurationHandler(preferences);
        Configuration configuration = configurationHandler.getConfiguration();
        configurationHandler.save();
        configuration.addTemplateDir(null);  // Ignore: 2. remove expected.
        configurationHandler.save();
        configuration.addTemplateDir("");    // Ignore: 3. remove expected.
        configurationHandler.save();
        configuration.addTemplateDir("   "); // Ignore: 4. remove expected.
        configurationHandler.save();
        configuration.addTemplateDir(DIR1);
        configurationHandler.save();
        configuration.addTemplateDir(DIR2);
        configuration.addTemplateDir("  ");
        configurationHandler.save();
        configuration.addTemplateDir(DIR3);
        configurationHandler.save();

        InOrder inOrder = Mockito.inOrder(preferences);
        inOrder.verify(preferences).remove(TEMPLATES_DIRS);
        inOrder.verify(preferences).remove(TEMPLATES_DIRS);
        inOrder.verify(preferences).remove(TEMPLATES_DIRS);
        inOrder.verify(preferences).remove(TEMPLATES_DIRS);
        inOrder.verify(preferences).put(TEMPLATES_DIRS, "[{\"directory\":\"" + DIR1 + "\",\"recursive\":false}]");
        inOrder.verify(preferences).put(TEMPLATES_DIRS, "[{\"directory\":\"" + DIR1 + "\",\"recursive\":false},{\"directory\":\"" +
                DIR2 + "\",\"recursive\":false}]");
        inOrder.verify(preferences).put(TEMPLATES_DIRS, "[{\"directory\":\"" + DIR1 + "\",\"recursive\":false},{\"directory\":\"" +
                DIR2 + "\",\"recursive\":false},{\"directory\":\"" + DIR3b + "\",\"recursive\":false}]");
        inOrder.verify(preferences).flush();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void configurationLoadTest() throws Exception {
        Preferences preferences = mock(Preferences.class);
        ConfigurationHandler configurationHandler = new ConfigurationHandler(preferences);
        Configuration configuration = configurationHandler.getConfiguration();
        when(preferences.get(TEMPLATES_DIRS, null)).thenReturn(null)
                .thenReturn("[{\"directory\":\"" + DIR1 + "\",\"recursive\":false}]")
                .thenReturn("[{\"directory\":\"" + DIR1 + "\",\"recursive\":false},{\"directory\":\"" +
                        DIR2 + "\",\"recursive\":false},{\"directory\":\"" + DIR3b + "\",\"recursive\":false}]");
        configurationHandler.load();
        assertNull(configuration.getTemplateDirs());
        configurationHandler.load();
        assertEquals(1, configuration.getTemplateDirs().size());
        assertEquals(DIR1, configuration.getTemplateDirs().get(0).getDirectory());
        configurationHandler.load();
        assertEquals(3, configuration.getTemplateDirs().size());
        assertEquals(DIR1, configuration.getTemplateDirs().get(0).getDirectory());
        assertEquals(DIR2, configuration.getTemplateDirs().get(1).getDirectory());
        assertEquals(DIR3, configuration.getTemplateDirs().get(2).getDirectory());
    }
}
