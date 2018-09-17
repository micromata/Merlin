package de.reinhard.merlin.app;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.reinhard.merlin.app.json.ConfigurationTestDeserializer;
import de.reinhard.merlin.app.json.ConfigurationTestSerializer;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private int port;
    private String language;
    private List<ConfigurationTemplateDir> templateDirs;

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

    @JsonSerialize(using = ConfigurationTestSerializer.class)
    @JsonDeserialize(using = ConfigurationTestDeserializer.class)
    public List<ConfigurationTemplateDir> getTemplateDirs() {
        return templateDirs;
    }

    public void setTemplateDirs(List<ConfigurationTemplateDir> templateDirs) {
        this.templateDirs = templateDirs;
    }

    public void addTemplateDir(String templateDir) {
        addTemplateDir(templateDir, false);
    }

    public void addTemplateDir(String templateDir, boolean recursive) {
        if (StringUtils.isBlank(templateDir)) {
            return;
        }
        if (templateDirs == null) {
            templateDirs = new ArrayList<>();
        }
        templateDirs.add(new ConfigurationTemplateDir().setDirectory(templateDir).setRecursive(recursive));
    }

    public void copyFrom(Configuration configuration) {
        this.language = configuration.language;
        this.port = configuration.port;
        this.templateDirs = configuration.templateDirs;
    }
}
