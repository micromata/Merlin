package de.reinhard.merlin.word.templating;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SerialData {
    private static Logger log = LoggerFactory.getLogger(SerialData.class);

    private List<SerialDataEntry> entries = new ArrayList<>();
    private String filenamePattern;
    private Template template;
    private TemplateDefinition templateDefinition;

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

    public TemplateDefinition getTemplateDefinition() {
        return templateDefinition;
    }

    public void setTemplateDefinition(TemplateDefinition templateDefinition) {
        this.templateDefinition = templateDefinition;
    }

    /**
     * @return Filename with suffix '-serial.xlsx' and the name base name as the template file.
     */
    public String createFilename() {
        if (template == null) {
            return "serial.xlsx";
        }
        return FilenameUtils.getBaseName(template.getFileDescriptor().getFilename()) + "-serial.xlsx";
    }
}
