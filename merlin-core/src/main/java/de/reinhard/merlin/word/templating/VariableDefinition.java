package de.reinhard.merlin.word.templating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.ArrayList;
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
            this.allowedValuesList = new ArrayList<>();
        }
        for (Object val : allowedValues) {
            this.allowedValuesList.add(val);
        }
        return this;
    }

    public VariableType getType() {
        return type;
    }

    public VariableDefinition setType(VariableType type) {
        this.type = type;
        return this;
    }

    @Transient
    public String getTypeAsString() {
        if (type == null) {
            return "";
        }
        return type.toString().toLowerCase();
    }

    public VariableDefinition setTypeFromString(String typeString) {
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
}
