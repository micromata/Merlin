package de.reinhard.merlin.word.templating;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Only a reference to a TemplateDefinition by id or name.
 */
public class TemplateDefinitionReference {
    private String templateDefinitionId;
    private String templateDefinitionName;

    public String getTemplateDefinitionId() {
        return templateDefinitionId;
    }

    public void setTemplateDefinitionId(String templateDefinitionId) {
        this.templateDefinitionId = templateDefinitionId;
    }

    public String getTemplateDefinitionName() {
        return templateDefinitionName;
    }

    public void setTemplateDefinitionName(String templateDefinitionName) {
        this.templateDefinitionName = templateDefinitionName;
    }

    @Override
    public String toString() {
        ToStringBuilder tos = new ToStringBuilder(this);
        tos.append("templateDefinitionId", templateDefinitionId);
        tos.append("templateDefinitionName", templateDefinitionName);
        return tos.toString();
    }
}
