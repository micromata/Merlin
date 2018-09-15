package de.reinhard.merlin.app;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private int port;
    private String language;
    private List<String> templateDirs;

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

    public List<String> getTemplateDirs() {
        return templateDirs;
    }

    public void setTemplateDirs(List<String> templateDirs) {
        this.templateDirs = templateDirs;
    }

    public void addTemplateDir(String templateDir) {
        if (StringUtils.isBlank(templateDir)) {
            return;
        }
        if (templateDirs == null) {
            templateDirs = new ArrayList<>();
        }
        templateDirs.add(templateDir);
    }

    public void copyFrom(Configuration configuration) {
        this.language = configuration.language;
        this.port = configuration.port;
    }
}
