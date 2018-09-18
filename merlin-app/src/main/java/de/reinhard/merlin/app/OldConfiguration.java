package de.reinhard.merlin.app;

import org.apache.commons.collections4.CollectionUtils;

/**
 * Only for backwards compability in the development phase.
 */
public class OldConfiguration {
    private int port;
    private String language;
    private String templatesDir;

    public OldConfiguration() {
    }

    public OldConfiguration(Configuration configuration) {
        this.language = configuration.getLanguage();
        this.port = configuration.getPort();
        this.templatesDir = CollectionUtils.isNotEmpty(configuration.getTemplatesDirs())
                ? configuration.getTemplatesDirs().get(0).getDirectory() : null;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTemplatesDir() {
        return templatesDir;
    }

    public void setTemplatesDir(String templatesDir) {
        this.templatesDir = templatesDir;
    }

    public void copyTo(Configuration configuration) {
        configuration.setLanguage(this.language);
        configuration.setPort(this.port);
        if (CollectionUtils.isNotEmpty(configuration.getTemplatesDirs())) {
            configuration.getTemplatesDirs().get(0).setDirectory(this.templatesDir);
        } else {
            configuration.addTemplatesDir(this.templatesDir);
        }
    }
}
