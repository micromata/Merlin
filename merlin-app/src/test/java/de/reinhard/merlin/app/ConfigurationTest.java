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
        configuration.addTemplateDir("/Users/kai/Documents/templates");
        configurationHandler.save();
        configuration.addTemplateDir("/Users/kai/Templates");
        configuration.addTemplateDir("  ");
        configurationHandler.save();
        configuration.addTemplateDir("/Users/kai/Templates of \"Kai\"");
        configurationHandler.save();

        InOrder inOrder = Mockito.inOrder(preferences);
        inOrder.verify(preferences).remove(TEMPLATES_DIRS);
        inOrder.verify(preferences).remove(TEMPLATES_DIRS);
        inOrder.verify(preferences).remove(TEMPLATES_DIRS);
        inOrder.verify(preferences).remove(TEMPLATES_DIRS);
        inOrder.verify(preferences).put(TEMPLATES_DIRS, "\"/Users/kai/Documents/templates\"");
        inOrder.verify(preferences).put(TEMPLATES_DIRS, "\"/Users/kai/Documents/templates\";\"/Users/kai/Templates\"");
        inOrder.verify(preferences).put(TEMPLATES_DIRS, "\"/Users/kai/Documents/templates\";\"/Users/kai/Templates\";\"/Users/kai/Templates of \"\"Kai\"\"\"");
        inOrder.verify(preferences).flush();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void configurationLoadTest() throws Exception {
        Preferences preferences = mock(Preferences.class);
        ConfigurationHandler configurationHandler = new ConfigurationHandler(preferences);
        Configuration configuration = configurationHandler.getConfiguration();
        when(preferences.get(TEMPLATES_DIRS, null)).thenReturn(null)
                .thenReturn("\"/Users/kai/Documents/templates\"")
                .thenReturn("\"/Users/kai/Documents/templates\";\"/Users/kai/Templates\";\"/Users/kai/Templates of \"\"Kai\"\"\"");
        configurationHandler.load();
        assertNull(configuration.getTemplateDirs());
        configurationHandler.load();
        assertEquals(1, configuration.getTemplateDirs().size());
        assertEquals("/Users/kai/Documents/templates", configuration.getTemplateDirs().get(0));
        configurationHandler.load();
        assertEquals(3, configuration.getTemplateDirs().size());
        assertEquals("/Users/kai/Documents/templates", configuration.getTemplateDirs().get(0));
        assertEquals("/Users/kai/Templates", configuration.getTemplateDirs().get(1));
        assertEquals("/Users/kai/Templates of \"Kai\"", configuration.getTemplateDirs().get(2));
    }
}
