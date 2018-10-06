package de.reinhard.merlin.app.rest;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TemplateRunnerData {
    private String templateDefinitionId;
    private Path templateCanonicalPath;
    private Map<String, Object> variables;

    public String getTemplateDefinitionId() {
        return templateDefinitionId;
    }

    public void setTemplateDefinitionId(String templateDefinitionId) {
        this.templateDefinitionId = templateDefinitionId;
    }

    public Path getTemplateCanonicalPath() {
        return templateCanonicalPath;
    }

    public void setTemplateCanonicalPath(Path templateCanonicalPath) {
        this.templateCanonicalPath = templateCanonicalPath;
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
