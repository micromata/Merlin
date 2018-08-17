package de.reinhard.merlin.data;

import java.util.HashMap;
import java.util.Map;

public class MainStorage {
    private Map<String, Object> map = new HashMap<>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
         return map.get(key);
    }
}
