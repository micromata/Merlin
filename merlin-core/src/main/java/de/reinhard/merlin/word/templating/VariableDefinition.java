package de.reinhard.merlin.word.templating;

import java.util.LinkedList;
import java.util.List;

public class VariableDefinition<T> {
    private String name;
    private String description;
    private boolean required;
    private boolean unique;
    private T standardValue;
    private T minimumValue;
    private T maximumValue;
    private List<T> allowedValuesList;

    public VariableDefinition() {

    }

    public VariableDefinition(String variableName) {
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
    public VariableDefinition<T> setRequired() {
        return setRequired(true);
    }

    /**
     * @param required
     * @return this for chaining.
     */
    public VariableDefinition<T> setRequired(boolean required) {
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
    public VariableDefinition<T> setUnique() {
        return setUnique(true);
    }

    /**
     * @param unique
     * @return this for chaining.
     */
    public VariableDefinition<T> setUnique(boolean unique) {
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
    public VariableDefinition<T> setDescription(String description) {
        this.description = description;
        return this;
    }

    public T getStandardValue() {
        return standardValue;
    }

    /**
     * @param standardValue
     * @return this for chaining.
     */
    public VariableDefinition<T> setStandardValue(T standardValue) {
        this.standardValue = standardValue;
        return this;
    }

    public T getMinimumValue() {
        return minimumValue;
    }

    /**
     * @param minimumValue
     * @return this for chaining.
     */
    public VariableDefinition<T> setMinimumValue(T minimumValue) {
        this.minimumValue = minimumValue;
        return this;
    }

    public T getMaximumValue() {
        return maximumValue;
    }

    /**
     * @param maximumValue
     * @return this for chaining.
     */
    public VariableDefinition<T> setMaximumValue(T maximumValue) {
        this.maximumValue = maximumValue;
        return this;
    }

    /**
     * If allowed values are defined, the variable has to contain one value of this list (e. g. used for options in a drop-down).
     * Example: color with allowed values {blue, red, white, yellow}
     *
     * @return allowed values if given.
     */
    public List<T> getAllowedValuesList() {
        return allowedValuesList;
    }

    public void addAllowedValues(List<T> allowedValues) {
        this.allowedValuesList = allowedValues;
    }

    /**
     * @param allowedValues
     * @return this for chaining.
     */
    public VariableDefinition<T> addAllowedValues(T... allowedValues) {
        if (this.allowedValuesList == null) {
            this.allowedValuesList = new LinkedList();
        }
        for (T val : allowedValues) {
            this.allowedValuesList.add(val);
        }
        return this;
    }
}
