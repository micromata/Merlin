package de.reinhard.merlin.word.templating;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Only a reference to a TemplateDefinition by id or name.
 */
public class TemplateDefinitionReference {
    private String templateDefinitionId;
    private String templateName;

    public String getTemplateDefinitionId() {
        return templateDefinitionId;
    }

    public void setTemplateDefinitionId(String templateDefinitionId) {
        this.templateDefinitionId = templateDefinitionId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public String toString() {
        ToStringBuilder tos = new ToStringBuilder(this);
        tos.append("templateDefinitionId", templateDefinitionId);
        tos.append("templateName", templateName);
        return tos.toString();
    }
}
