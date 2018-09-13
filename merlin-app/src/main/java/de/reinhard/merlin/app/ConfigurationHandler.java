package de.reinhard.merlin.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ConfigurationHandler {
    private Logger log = LoggerFactory.getLogger(ConfigurationHandler.class);
    private static final ConfigurationHandler instance = new ConfigurationHandler();
    private static final String WEBSERVER_PORT_PREF = "webserver-port";
    public static final int WEBSERVER_PORT_DEFAULT = 8042;
    private static final String LANGUAGE_PREF = "language";
    private static final String LANGUAGE_DEFAULT = "en";

    private Preferences preferences;
    private Configuration configuration = new Configuration();

    private ConfigurationHandler() {
        preferences = Preferences.userRoot().node("merlin");
        load();
    }

    public static ConfigurationHandler getInstance() {
        return instance;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void load() {
        configuration.setPort(preferences.getInt(WEBSERVER_PORT_PREF, WEBSERVER_PORT_DEFAULT));
        configuration.setLanguage(preferences.get(LANGUAGE_PREF, LANGUAGE_DEFAULT));
    }

    public void save() {
        preferences.putInt(WEBSERVER_PORT_PREF, configuration.getPort());
        preferences.put(LANGUAGE_PREF, configuration.getLanguage());
        try {
            preferences.flush();
        } catch (BackingStoreException ex) {
            log.error("Couldn't flush user preferences: " + ex.getMessage(), ex);
        }
    }
}
