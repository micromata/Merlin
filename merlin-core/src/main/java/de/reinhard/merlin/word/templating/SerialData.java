package de.reinhard.merlin.word.templating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SerialData {
    private static Logger log = LoggerFactory.getLogger(SerialData.class);

    private List<SerialDataEntry> entries = new ArrayList<>();
    private String filenamePattern;
    private Template template;

    public SerialData() {
    }

    public void add(SerialDataEntry data) {
        entries.add(data);
    }

    public List<SerialDataEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<SerialDataEntry> entries) {
        this.entries = entries;
    }

    public String getFilenamePattern() {
        return filenamePattern;
    }

    public void setFilenamePattern(String filenamePattern) {
        this.filenamePattern = filenamePattern;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public String getCanonicalTemplatePath() {
        if (template == null) {
            return null;
        }
        if (template.getFileDescriptor() == null) {
            return null;
        }
        return template.getFileDescriptor().getCanonicalPath();
    }

    public String getTemplateDefinitionId() {
        if (template == null) {
            return null;
        }
        if (template.getTemplateDefinition() == null) {
            return null;
        }
        return template.getTemplateDefinition().getId();
    }

    public String getTemplateDefinitionName() {
        if (template.getTemplateDefinition() == null) {
            return null;
        }
        return template.getTemplateDefinition().getName();
    }
}
