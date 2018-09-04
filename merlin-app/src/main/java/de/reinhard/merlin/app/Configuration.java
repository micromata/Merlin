package de.reinhard.merlin.app;

import java.util.prefs.Preferences;

public class Configuration {
    private static final Configuration instance = new Configuration();
    private static final String PREF_WEBSERVER_PORT = "webserver-port";
    private static final int DEFAULT_PORT = 8042;

    private int port;
    private Preferences preferences;

    private Configuration() {
        preferences = Preferences.userRoot().node("merlin");
        load();
    }

    public static Configuration getInstance() {
        return instance;
    }

    public void load() {
        this.port = preferences.getInt(PREF_WEBSERVER_PORT, DEFAULT_PORT);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        preferences.putInt(PREF_WEBSERVER_PORT, this.port);
    }
}
