package de.reinhard.merlin.data;

import java.util.HashMap;
import java.util.Map;

public class PropertiesStorage {
    private Map<String, String> properties = new HashMap<String, String>();

    public void setConfig(String key, String value) {
        properties.put(key, value);
    }

    public String getConfig(String key) {
        return properties.get(key);
    }

    public Map<String, String> getConfig() {
        return properties;
    }

}
