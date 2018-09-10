package de.reinhard.merlin.app;

public class Configuration {
    private int port;
    private String language;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLanguage() { return language; }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void copyFrom(Configuration configuration) {
        this.language = configuration.language;
        this.port = configuration.port;
    }
}
