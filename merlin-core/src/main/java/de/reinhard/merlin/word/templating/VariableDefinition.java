package de.reinhard.merlin.word.templating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class VariableDefinition {
    private static Logger log = LoggerFactory.getLogger(VariableDefinition.class);
    private String name;
    private String description;
    private boolean required;
    private boolean unique;
    private Object minimumValue;
    private Object maximumValue;
    private List<Object> allowedValuesList;
    private VariableType type;

    public VariableDefinition() {

    }

    public VariableDefinition(String variableName) {
        this(null, variableName);
    }

    public VariableDefinition(VariableType type, String variableName) {
        if (type == null) {
            this.type = VariableType.STRING;
        } else {
            this.type = type;
        }
        this.name = variableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    /**
     * @return this for chaining.
     */
    public VariableDefinition setRequired() {
        return setRequired(true);
    }

    /**
     * @param required
     * @return this for chaining.
     */
    public VariableDefinition setRequired(boolean required) {
        this.required = required;
        return this;
    }

    /**
     * Only useful for serial letters. Each variable should occur unique.
     *
     * @return
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * @return this for chaining.
     */
    public VariableDefinition setUnique() {
        return setUnique(true);
    }

    /**
     * @param unique
     * @return this for chaining.
     */
    public VariableDefinition setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    public String getDescription() {
        return description;
    }

    /**
     * @param description
     * @return this for chaining.
     */
    public VariableDefinition setDescription(String description) {
        this.description = description;
        return this;
    }

    public Object getMinimumValue() {
        return minimumValue;
    }

    /**
     * @param minimumValue
     * @return this for chaining.
     */
    public VariableDefinition setMinimumValue(Object minimumValue) {
        if (minimumValue != null && minimumValue instanceof String && ((String) minimumValue).length() == 0) {
            this.minimumValue = null;
            return this;
        }
        this.minimumValue = minimumValue;
        return this;
    }

    public Object getMaximumValue() {
        return maximumValue;
    }

    /**
     * @param maximumValue
     * @return this for chaining.
     */
    public VariableDefinition setMaximumValue(Object maximumValue) {
        if (maximumValue != null && maximumValue instanceof String && ((String) maximumValue).length() == 0) {
            this.maximumValue = null;
            return this;
        }
        this.maximumValue = maximumValue;
        return this;
    }

    /**
     * If allowed values are defined, the variable has to contain one value of this list (e. g. used for options in a drop-down).
     * Example: color with allowed values {blue, red, white, yellow}
     *
     * @return allowed values if given.
     */
    public List<Object> getAllowedValuesList() {
        return allowedValuesList;
    }

    public void addAllowedValues(List<Object> allowedValues) {
        this.allowedValuesList = allowedValues;
    }

    /**
     * @param allowedValues
     * @return this for chaining.
     */
    public VariableDefinition addAllowedValues(Object... allowedValues) {
        if (this.allowedValuesList == null) {
            this.allowedValuesList = new LinkedList();
        }
        for (Object val : allowedValues) {
            this.allowedValuesList.add(val);
        }
        return this;
    }

    public VariableType getType() {
        return type;
    }

    @Transient
    public String getTypeAsString() {
        if (type == null) {
            return "";
        }
        return type.toString().toLowerCase();
    }

    public VariableDefinition setType(VariableType type) {
        this.type = type;
        return this;
    }

    @Transient
    public VariableDefinition setType(String typeString) {
        if (typeString == null) {
            type = null;
        } else {
            String upper = typeString.toUpperCase();
            try {
                type = VariableType.valueOf(upper);
            } catch (IllegalArgumentException ex) {
                log.error("Can't convert '" + typeString + "' to VariableType: " + ex.getMessage());
            }
        }
        return this;
    }

    public Object convertValue(Object value) {
        if (value == null) {
            return null; // Nothing to do.
        }
        if (type == null) {
            setType(value);
        }
        Object retval = convertValue(value, type);
        if (retval != null) {
            return retval;
        }
        log.error("Value '" + value + "' of type " + value.getClass() + " doesn't match type: " + type);
        return null;
    }

    public static Object convertValue(Object value, VariableType type) {
        if (value == null) {
            return null;
        }
        switch (type) {
            case INT:
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                if (value instanceof String) {
                    try {
                        return new Integer((String) value);
                    } catch (NumberFormatException ex) {
                        log.error("Can't parse integer '" + value + "': " + ex.getMessage(), ex);
                        return 0;
                    }
                }
                log.error("Can't get integer from type " + value.getClass().getCanonicalName() + ": " + value);
                return 0;
            case FLOAT:
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                if (value instanceof String) {
                    try {
                        return new Double((String) value);
                    } catch (NumberFormatException ex) {
                        log.error("Can't parse float '" + value + "': " + ex.getMessage(), ex);
                        return 0.0;
                    }
                }
                log.error("Can't get float from type " + value.getClass().getCanonicalName() + ": " + value);
                return 0.0;
            case STRING:
                return value.toString();
            case DATE:
                if (value instanceof Date) {
                    return value;
                } else if (value instanceof String) {
                    if (((String) value).trim().length() == 0) {
                        return null;
                    }
                }
                log.error("Can't get date from type " + value.getClass().getCanonicalName() + ": " + value);
        }
        return value;
    }

    private void setType(Object value) {
        if (value == null) {
            return; // Nothing to do.
        }
        if (value instanceof String) {
            type = VariableType.STRING;
        } else if (value instanceof Integer || value instanceof Long) {
            type = VariableType.INT;
        } else if (value instanceof Float || value instanceof Double) {
            type = VariableType.FLOAT;
        } else if (value instanceof LocalDate) {
            type = VariableType.DATE;
        } else {
            log.error("Variable value of type " + value.getClass().getCanonicalName() + " not yet supported.");
        }
    }
}
