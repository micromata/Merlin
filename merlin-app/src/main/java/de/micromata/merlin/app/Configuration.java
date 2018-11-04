package de.reinhard.merlin.app;

import org.apache.commons.lang3.StringUtils;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Configuration {
    private final static String[] SUPPORTED_LANGUAGES = {"en", "de"};

    private int port;
    private boolean showTestData = true;
    private List<ConfigurationTemplatesDir> templatesDirs;
    private boolean templatesDirModified = false;

    public static String[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    public void resetModifiedFlag() {
        templatesDirModified = false;
    }

    @Transient
    public boolean isTemplatesDirModified() {
        return templatesDirModified;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return true, if test templates will be shown and usable
     */
    public boolean isShowTestData() {
        return showTestData;
    }

    public void setShowTestData(boolean showTestData) {
        this.showTestData = showTestData;
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
        this.port = other.port;
        this.showTestData = other.showTestData;
        if (!Objects.equals(this.templatesDirs, other.templatesDirs)) {
            templatesDirModified = true;
        }
        this.templatesDirs = other.templatesDirs;
    }
}
