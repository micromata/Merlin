package de.reinhard.merlin.app;

import com.fasterxml.jackson.core.type.TypeReference;
import de.reinhard.merlin.app.json.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ConfigurationHandler {
    private Logger log = LoggerFactory.getLogger(ConfigurationHandler.class);
    private static final ConfigurationHandler instance = new ConfigurationHandler();
    private static final String WEBSERVER_PORT_PREF = "webserver-port";
    public static final int WEBSERVER_PORT_DEFAULT = 8042;
    private static final String LANGUAGE_PREF = "language";
    private static final String LANGUAGE_DEFAULT = "en";
    static final String TEMPLATES_DIRS = "template-directories";

    private Preferences preferences;
    private Configuration configuration = new Configuration();

    /**
     * Only for test case.
     *
     * @param preferences
     */
    ConfigurationHandler(Preferences preferences) {
        this.preferences = preferences;
    }

    private ConfigurationHandler() {
        preferences = Preferences.userRoot().node("de").node("micromata").node("merlin");
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
        String json = preferences.get(TEMPLATES_DIRS, null);
        if (json != null) {
            try {
                List<ConfigurationTemplatesDir> templateDirs =
                        JsonUtils.fromJson(new TypeReference<List<ConfigurationTemplatesDir>>() {
                        }, json);
                configuration.setTemplatesDirs(templateDirs);
            } catch (Exception ex) {
                log.error("Exception while parsing template dirs from json: " + ex.getMessage(), ex);
            }
        } else {
            configuration.setTemplatesDirs(null);
        }
    }

    public void save() {
        log.info("Saving configuration to user prefs.");
        preferences.putInt(WEBSERVER_PORT_PREF, configuration.getPort());
        preferences.put(LANGUAGE_PREF, configuration.getLanguage());
        if (CollectionUtils.isNotEmpty(configuration.getTemplatesDirs())) {
            String  json = JsonUtils.toJson(configuration.getTemplatesDirs());
            preferences.put(TEMPLATES_DIRS, json);
        } else {
            preferences.remove(TEMPLATES_DIRS);
        }
        try {
            preferences.flush();
        } catch (BackingStoreException ex) {
            log.error("Couldn't flush user preferences: " + ex.getMessage(), ex);
        }
    }
}
