package de.micromata.merlin.word.templating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Variables {
    private static Logger log = LoggerFactory.getLogger(Variables.class);

    private Map<String, Object> variables = new HashMap<>();

    private Map<String, String> formattedVariables = new HashMap<>();

    public Variables() {
    }

    public void put(String variable, Object value) {
        variables.put(variable, value);
    }

    public void putFormatted(String variable, String formattedValue) {
        variables.put(variable, formattedValue);
    }

    public Object get(String variable) {
        return variables.get(variable);
    }

    public String getFormattedValue(String variable) {
        String result = formattedVariables.get(variable);
        if (result != null) return result;
        Object value = get(variable);
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void putAll(Map<? extends String, ?> map) {
        variables.putAll(map);
    }
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
