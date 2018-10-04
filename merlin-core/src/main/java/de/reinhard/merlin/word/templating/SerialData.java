package de.reinhard.merlin.word.templating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SerialData {
    private static Logger log = LoggerFactory.getLogger(SerialData.class);

    private List<SerialDataEntry> entries = new ArrayList<>();
    private String filenamePattern;
    private String templateCanonicalPath;
    private String templateDefinitionId;

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

    public String getTemplateCanonicalPath() {
        return templateCanonicalPath;
    }

    public void setTemplateCanonicalPath(String templateCanonicalPath) {
        this.templateCanonicalPath = templateCanonicalPath;
    }

    public String getTemplateDefinitionId() {
        return templateDefinitionId;
    }

    public void setTemplateDefinitionId(String templateDefinitionId) {
        this.templateDefinitionId = templateDefinitionId;
    }
}
