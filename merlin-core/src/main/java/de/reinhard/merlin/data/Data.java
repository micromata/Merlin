package de.reinhard.merlin.data;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Data {
    // Stores max length of all properties of same type for formatting output (rightPad).
    private static Map<String, Integer> maxPropertyLength = new HashMap<>();

    private Map<String, Object> properties = new HashMap<>();

    private String type;

    public Data(String type) {
        this.type = type;
    }

    public void put(String property, Object value) {
        properties.put(property, value);
        if (value != null) {
            int maxLength = getMaxLength(type, property);
            int length = value.toString().length();
            if (length > maxLength) {
                setMaxLength(type, property, length);
            }
        }
    }

    public String getString(String property) {
        Object val = getValue(property);
        return val != null ? val.toString() : null;
    }

    public String getRightPadString(String property) {
        return getRightPadString(property, 0);
    }

    public String getRightPadString(String prefix, String property, String suffix) {
        int offset = StringUtils.length(prefix) + StringUtils.length(suffix);
        String str = getString(property);
        return StringUtils.rightPad(StringUtils.defaultString(prefix) + str + StringUtils.defaultString(suffix), getMaxLength(type, property) + offset);
    }

    /**
     * @param property
     * @param offset
     * @return Formatted string with fixed length of length of longest property of this type.
     */
    public String getRightPadString(String property, int offset) {
        String str = getString(property);
        return StringUtils.rightPad(str, getMaxLength(type, property) + offset);
    }

    public int getMaxLength(String property) {
        return getMaxLength(type, property);
    }

    public Object getValue(String property) {
        return properties.get(property);
    }

    private static int getMaxLength(String type, String property) {
        Integer maxLength = maxPropertyLength.get(type + "." + property);
        return maxLength != null ? maxLength : 0;
    }

    private static void setMaxLength(String type, String property, int maxLength) {
        maxPropertyLength.put(type + "." + property, maxLength);
    }
}
