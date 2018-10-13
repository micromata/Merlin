package de.reinhard.merlin.app.rest;

import java.util.HashMap;
import java.util.Map;

public class TemplateRunnerData {
    private String templateDefinitionId;
    private String templateHashId;
    private Map<String, Object> variables;

    public String getTemplateDefinitionId() {
        return templateDefinitionId;
    }

    public void setTemplateDefinitionId(String templateDefinitionId) {
        this.templateDefinitionId = templateDefinitionId;
    }

    public String getTemplateHashId() {
        return templateHashId;
    }

    public void setTemplateHashId(String templateHashId) {
        this.templateHashId = templateHashId;
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
