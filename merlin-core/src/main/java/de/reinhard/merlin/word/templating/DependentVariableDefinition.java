package de.reinhard.merlin.word.templating;

import java.beans.Transient;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Defines a variable which is dependent of another variable (master variable).
 */
public class DependentVariableDefinition {
    private String name;
    private VariableDefinition dependsOn;
    private Map<Object, String> mapping;

    /**
     * @return Name of the variable to use via ${name} in the templats..
     */
    public String getName() {
        return name;
    }

    public DependentVariableDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public VariableDefinition getDependsOn() {
        return dependsOn;
    }

    public DependentVariableDefinition setDependsOn(VariableDefinition dependsOn) {
        this.dependsOn = dependsOn;
        return this;
    }

    /**
     * @return The mapping of the values of the master variable to this variable.
     */
    public Map<Object, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<Object, String> mapping) {
        this.mapping = mapping;
    }

    public DependentVariableDefinition addMapping(Object masterValue, String value) {
        createAndGetMapping().put(masterValue, value);
        return this;
    }

    private Map<Object, String> createAndGetMapping() {
        if (mapping == null) {
            mapping = new HashMap<>();
        }
        return mapping;
    }

    public List<String> getMappingList() {
        List<String> list = new LinkedList<>();
        if (dependsOn == null) {
            return list;
        }
        for (Object masterValue : dependsOn.getAllowedValuesList()) {
            String mappedValue = mapping.get(masterValue);
            if (mappedValue != null) {
                list.add(mappedValue);
            } else {
                list.add("");
            }
        }
        return list;
    }

    public String getMappedValue(Map<String, String> variables) {
        if (mapping == null || variables == null || dependsOn == null) {
            return "";
        }
        String value = variables.get(dependsOn.getName());
        return mapping.get(value);
    }

    @Transient
    public String getMappingInformation() {
        if (dependsOn == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Object masterValue : dependsOn.getAllowedValuesList()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            String mappedValue = mapping.get(masterValue);
            sb.append("\"").append(masterValue).append("\"->\"").append(mappedValue != null ? mappedValue : "").append("\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
