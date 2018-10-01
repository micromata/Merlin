package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;

import java.util.List;

public class Data {
    private List<TemplateDefinition> templateDefinitions;
    private List<Template> templates;

    public List<TemplateDefinition> getTemplateDefinitions() {
        return templateDefinitions;
    }

    public void setTemplateDefinitions(List<TemplateDefinition> templateDefinitions) {
        this.templateDefinitions = templateDefinitions;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }
}
