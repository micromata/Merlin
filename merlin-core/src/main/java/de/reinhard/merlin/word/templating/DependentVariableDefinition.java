package de.reinhard.merlin.word.templating;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a variable which is dependent of another variable (master variable).
 *
 * @param <T>
 */
public class DependentVariableDefinition<T> {
    private String name;
    private VariableDefinition<T> dependsOn;
    private Map<T, String> mapping;

    /**
     * @return Name of the variable to use via ${name} in the templats..
     */
    public String getName() {
        return name;
    }

    public DependentVariableDefinition<T> setName(String name) {
        this.name = name;
        return this;
    }

    public VariableDefinition getDependsOn() {
        return dependsOn;
    }

    public DependentVariableDefinition<T> setDependsOn(VariableDefinition dependsOn) {
        this.dependsOn = dependsOn;
        return this;
    }

    /**
     * @return The mapping of the values of the master variable to this variable.
     */
    public Map<T, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<T, String> mapping) {
        this.mapping = mapping;
    }

    public DependentVariableDefinition<T> addMapping(T masterValue, String value) {
        createAndGetMapping().put(masterValue, value);
        return this;
    }

    private Map<T, String> createAndGetMapping() {
        if (mapping == null) {
            mapping = new HashMap<>();
        }
        return mapping;
    }
}
