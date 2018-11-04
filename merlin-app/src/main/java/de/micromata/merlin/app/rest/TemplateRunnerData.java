package de.micromata.merlin.app.rest;

import java.util.HashMap;
import java.util.Map;

public class TemplateRunnerData {
    private String templateDefinitionId;
    private String templatePrimaryKey;
    private Map<String, Object> variables;

    public String getTemplateDefinitionId() {
        return templateDefinitionId;
    }

    public void setTemplateDefinitionId(String templateDefinitionId) {
        this.templateDefinitionId = templateDefinitionId;
    }

    public String getTemplatePrimaryKey() {
        return templatePrimaryKey;
    }

    public void setTemplatePrimaryKey(String templatePrimaryKey) {
        this.templatePrimaryKey = templatePrimaryKey;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public void put(String variable, Object value) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put(variable, value);
    }
}
