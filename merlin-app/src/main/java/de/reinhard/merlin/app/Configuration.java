package de.reinhard.merlin.app;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Configuration {
    private final static String[] SUPPORTED_LANGUAGES = {"en", "de"};

    private int port;
    private String serverLanguage;
    private boolean showTestData = true;
    private List<ConfigurationTemplatesDir> templatesDirs;
    private boolean templatesDirModified = false;

    public static String[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    public void resetModifiedFlag() {
        templatesDirModified = false;
    }

    public boolean isTemplatesDirModified() {
        return templatesDirModified;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerLanguage() {
        return serverLanguage;
    }

    public void setServerLanguage(String serverLanguage) {
        if (serverLanguage == null || !ArrayUtils.contains(SUPPORTED_LANGUAGES, serverLanguage))
            this.serverLanguage = null;
        else
            this.serverLanguage = serverLanguage;
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
        setServerLanguage(other.serverLanguage);
        this.port = other.port;
        this.showTestData = other.showTestData;
        if (!Objects.equals(this.templatesDirs, other.templatesDirs)) {
            templatesDirModified = true;
        }
        this.templatesDirs = other.templatesDirs;
    }
}
