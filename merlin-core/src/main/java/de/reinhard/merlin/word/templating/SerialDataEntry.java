package de.reinhard.merlin.word.templating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SerialDataEntry {
    private static Logger log = LoggerFactory.getLogger(SerialDataEntry.class);

    private Map<String, Object> variables = new HashMap<>();

    public SerialDataEntry() {
    }

    public void put(String variable, Object value) {
        variables.put(variable, value);
    }

    public Object get(String variable) {
        return variables.get(variable);
    }

    public Map<String, Object> getVariables() {
        return variables;
    }
}
