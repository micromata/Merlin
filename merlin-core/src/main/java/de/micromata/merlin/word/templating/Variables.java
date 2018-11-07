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
        formattedVariables.put(variable, formattedValue);
    }

    public boolean contains(String variable) {
        return variables.containsKey(variable) || formattedVariables.containsKey(variable);
    }

    /**
     * The variable if found. If not and the formatted value is given, the formatted value will be returned. If none of the values is given, null is returned.
     *
     * @param variable
     * @return value
     */
    public Object get(String variable) {
        Object value = variables.get(variable);
        if (value != null)
            return value;
        return formattedVariables.get(variable);
    }

    /**
     * Return the formatted value if given. If not the unformatted value will be taken, if given. If not the defaultString will be returned.
     * @param variable
     * @param defaultString "" as default.
     * @return value
     */
    public String getFormatted(String variable, String defaultString) {
        String result = formattedVariables.get(variable);
        if (result != null) return result;
        Object value = get(variable);
        if (value == null) {
            return defaultString;
        }
        return String.valueOf(value);
    }

    public String getFormatted(String variable) {
        return getFormatted(variable, "");
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
