package de.reinhard.merlin.app;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private int port;
    private String language;
    private List<ConfigurationTemplatesDir> templatesDirs;

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

    public List<ConfigurationTemplatesDir> getTemplatesDirs() {
        return templatesDirs;
    }

    public void setTemplatesDirs(List<ConfigurationTemplatesDir> templatesDirs) {
        this.templatesDirs = templatesDirs;
    }

    public void addTemplatesDir(String templateDir) {
        addTemplatesDir(templateDir, false);
    }

    public void addTemplatesDir(String templateDir, boolean recursive) {
        if (StringUtils.isBlank(templateDir)) {
            return;
        }
        if (templatesDirs == null) {
            templatesDirs = new ArrayList<>();
        }
        templatesDirs.add(new ConfigurationTemplatesDir().setDirectory(templateDir).setRecursive(recursive));
    }

    public void copyFrom(Configuration configuration) {
        this.language = configuration.language;
        this.port = configuration.port;
        this.templatesDirs = configuration.templatesDirs;
    }
}
