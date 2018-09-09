package de.reinhard.merlin.word.templating;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a variable which is dependent of another variable (master variable).
 *
 * @param <T>
 */
public class DependentVariable<T> {
    private VariableDefinition<T> dependsOn;
    private Map<T, String> mapping;

    public VariableDefinition getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(VariableDefinition dependsOn) {
        this.dependsOn = dependsOn;
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

    public void addMapping(T masterValue, String value) {
        createAndGetMapping().put(masterValue, value);
    }

    private Map<T, String> createAndGetMapping() {
        if (mapping == null) {
            mapping = new HashMap<>();
        }
        return mapping;
    }
}
