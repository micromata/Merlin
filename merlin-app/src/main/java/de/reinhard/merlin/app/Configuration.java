package de.reinhard.merlin.app;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Configuration {
    private int port;
    private String language;
    private List<ConfigurationTemplatesDir> templatesDirs;
    private boolean templatesDirModified = false;

    public void resetModifiedFlag() {
        templatesDirModified = false;
    }

    public boolean isTemplatesDirModified() {
        return templatesDirModified;
    }

    void setTemplatesDirModified(boolean templatesDirModified) {
        this.templatesDirModified = templatesDirModified;
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

    public List<ConfigurationTemplatesDir> getTemplatesDirs() {
        return templatesDirs;
    }

    public void setTemplatesDirs(List<ConfigurationTemplatesDir> templatesDirs) {
        if (!Objects.equals(this.templatesDirs, templatesDirs)) {
            templatesDirModified = true;
        }
        this.templatesDirs = templatesDirs;
    }

    public void addTemplatesDir(String templateDir) {
        templatesDirModified = true;
        addTemplatesDir(templateDir, false);
    }

    public void addTemplatesDir(String templateDir, boolean recursive) {
        if (StringUtils.isBlank(templateDir)) {
            return;
        }
        if (templatesDirs == null) {
            templatesDirs = new ArrayList<>();
        }
        templatesDirModified = true;
        templatesDirs.add(new ConfigurationTemplatesDir().setDirectory(templateDir).setRecursive(recursive));
    }

    public void copyFrom(Configuration other) {
        this.language = other.language;
        this.port = other.port;
        if (!Objects.equals(this.templatesDirs, other.templatesDirs)) {
            templatesDirModified = true;
        }
        this.templatesDirs = other.templatesDirs;
    }
}
