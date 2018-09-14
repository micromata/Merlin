package de.reinhard.merlin.word.templating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SerialDataEntry {
    private static Logger log = LoggerFactory.getLogger(SerialDataEntry.class);

    private Map<String, String> variables = new HashMap<>();

    public SerialDataEntry() {
    }

    public void put(String variable, String value) {
        variables.put(variable, value);
    }

    public void put(String variable, int value) {
        variables.put(variable, String.valueOf(value));
    }

    public String get(String variable) {
        return variables.get(variable);
    }

    public Map<String, String> getVariables() {
        return variables;
    }
}
