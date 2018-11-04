package de.micromata.merlin.data;

import de.micromata.merlin.excel.ExcelConfigReader;

import java.util.HashMap;
import java.util.Map;

/**
 * It's simply an {@link HashMap} filled by {@link ExcelConfigReader}.
 */
public class PropertiesStorage {
    private Map<String, Object> properties = new HashMap<>();

    public void setConfig(String key, String value) {
        properties.put(key, value);
    }

    public Object getConfig(String key) {
        return properties.get(key);
    }

    public String getConfigString(String key) {
        Object value = properties.get(key);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    public Map<String, Object> getConfig() {
        return properties;
    }

}
