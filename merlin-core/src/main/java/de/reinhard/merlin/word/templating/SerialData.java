package de.reinhard.merlin.word.templating;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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
    private String referencedTemplateDefinitionPrimaryKey, referencedTemplatePrimaryKey;

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
     *
     * @return Referenced template definition by primary key or id if specified inside configuration sheet.
     */
    public String getReferencedTemplateDefinitionPrimaryKey() {
        return referencedTemplateDefinitionPrimaryKey;
    }

    public void setReferencedTemplateDefinitionPrimaryKey(String referencedTemplateDefinitionPrimaryKey) {
        this.referencedTemplateDefinitionPrimaryKey = referencedTemplateDefinitionPrimaryKey;
    }

    /**
     *
     * @return Referenced template by primary key if specified inside configuration sheet.
     */
    public String getReferencedTemplatePrimaryKey() {
        return referencedTemplatePrimaryKey;
    }

    public void setReferencedTemplatePrimaryKey(String referencedTemplatePrimaryKey) {
        this.referencedTemplatePrimaryKey = referencedTemplatePrimaryKey;
    }

    /**
     * @return Filename with suffix '-serial.xlsx' and the name base name as the template file.
     */
    public String createFilenameForSerialTemplate() {
        if (template == null) {
            return "serial.xlsx";
        }
        return FilenameUtils.getBaseName(template.getFileDescriptor().getFilename()) + "-serial.xlsx";
    }

    public void createFilenamePattern() {
        if (StringUtils.isNotBlank(filenamePattern)) {
            return;
        }
        if (templateDefinition != null && StringUtils.isNotBlank(templateDefinition.getFilenamePattern())) {
            filenamePattern = templateDefinition.getFilenamePattern() + "-${counter}";
            return;
        }
        filenamePattern = FilenameUtils.getBaseName(template.getFileDescriptor().getFilename()) + "-${counter}";
    }
}
