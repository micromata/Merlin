package de.reinhard.merlin.word.templating;

import java.beans.Transient;
import java.util.*;

/**
 * Defines a variable which is dependent of another variable (master variable).
 */
public class DependentVariableDefinition {
    private String name;
    private VariableDefinition dependsOn;
    private Map<Object, Object> mapping;

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
    public Map<Object, Object> getMapping() {
        return mapping;
    }

    public void setMapping(Map<Object, Object> mapping) {
        this.mapping = mapping;
    }

    public DependentVariableDefinition addMapping(Object masterValue, Object value) {
        createAndGetMapping().put(masterValue, value);
        return this;
    }

    private Map<Object, Object> createAndGetMapping() {
        if (mapping == null) {
            mapping = new HashMap<>();
        }
        return mapping;
    }

    public List<Object> getMappingList() {
        List<Object> list = new ArrayList<>();
        if (dependsOn == null) {
            return list;
        }
        for (Object masterValue : dependsOn.getAllowedValuesList()) {
            Object mappedValue = mapping.get(masterValue);
            if (mappedValue != null) {
                list.add(mappedValue);
            } else {
                list.add("");
            }
        }
        return list;
    }

    public Object getMappedValue(Map<String, Object> variables) {
        if (mapping == null || variables == null || dependsOn == null) {
            return "";
        }
        Object value = variables.get(dependsOn.getName());
        return mapping.get(value);
    }

    @Transient
    public String getMappingInformation(TemplateRunContext context) {
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
            Object mappedValue = mapping.get(masterValue);
            sb.append("\"").append(context.toString(masterValue, dependsOn.getType())).append("\"->\"")
                    .append(mappedValue != null ? mappedValue : "").append("\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
