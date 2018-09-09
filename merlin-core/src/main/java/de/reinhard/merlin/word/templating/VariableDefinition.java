package de.reinhard.merlin.word.templating;

import java.util.LinkedList;
import java.util.List;

public class VariableDefinition<T> {
    private String variableName;
    private String description;
    private boolean required;
    private boolean unique;
    private T standardValue;
    private T minimumValue;
    private T maximumValue;
    private List<T> allowedValues;

    public VariableDefinition() {

    }

    public VariableDefinition(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
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
    public List<T> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<T> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public void setAllowedValues(T... allowedValues) {
        this.allowedValues = new LinkedList();
        for (T val : allowedValues) {
            this.allowedValues.add(val);
        }
    }
}
