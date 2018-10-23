package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.word.Conditionals;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Some statistics for templates
 */
public class TemplateStatistics implements Cloneable {
    private Logger log = LoggerFactory.getLogger(TemplateStatistics.class);
    private Conditionals conditionals;
    private Template template;
    private Collection<String> allDefinedVariables; // All variables defined in TemplateDefinition.
    private List<String> usedVariables;             // All variables used in the Word template.
    private Collection<String> unusedVariables;     // Variables defined but not used in the Word template.
    private Collection<String> undefinedVariables;  // Variables used in the Word template but not defined.
    private Collection<String> masterVariables;     // All variables other variables depend on.
    private Collection<String> dependentVariables;  // All dependent variables.
    private Collection<VariableDefinition> inputVariables; // All variables a user input is required for.

    public TemplateStatistics(Template parent) {
        this.template = parent;
    }

    /**
     * Analyzes used variables by this template and compares it to the defined variables in the given templateDefinition.
     */
    public void updateStatistics() {
        buildInputVariables();
        if (template.getTemplateDefinition() == null) {
            log.debug("No templateDefinition given. Can't update statistics. Clearing statistics.");
            this.allDefinedVariables = null;
            this.unusedVariables = null;
            this.undefinedVariables = null;
            this.masterVariables = null;
            this.dependentVariables = null;
            return;
        }
        this.allDefinedVariables = template.getTemplateDefinition().getAllDefinedVariableNames();
        if (log.isDebugEnabled()) {
            for (String variable : this.allDefinedVariables) {
                log.debug("Defined variable: " + variable);
            }
        }
        this.masterVariables = template.getTemplateDefinition().getAllMasterVariableNames();
        this.dependentVariables = template.getTemplateDefinition().getAllDependentVariableNames();
        this.unusedVariables = CollectionUtils.subtract(allDefinedVariables, usedVariables);
        this.unusedVariables = CollectionUtils.subtract(unusedVariables, masterVariables);
        if (log.isDebugEnabled()) {
            for (String variable : unusedVariables) {
                log.debug("Unused: " + variable);
            }
        }
        this.undefinedVariables = CollectionUtils.subtract(usedVariables, allDefinedVariables);
        if (log.isDebugEnabled()) {
            for (String variable : undefinedVariables) {
                log.debug("Undefined: " + variable);
            }
        }
    }

    public Conditionals getConditionals() {
        return conditionals;
    }

    public void setConditionals(Conditionals conditionals) {
        this.conditionals = conditionals;
    }

    public Collection<String> getAllDefinedVariables() {
        return allDefinedVariables;
    }

    public void setAllDefinedVariables(Collection<String> allDefinedVariables) {
        this.allDefinedVariables = allDefinedVariables;
    }

    public List<String> getUsedVariables() {
        return usedVariables;
    }

    public void setUsedVariables(List<String> usedVariables) {
        this.usedVariables = usedVariables;
    }

    public Collection<String> getUnusedVariables() {
        return unusedVariables;
    }

    public Collection<String> getUndefinedVariables() {
        return undefinedVariables;
    }

    public Collection<String> getMasterVariables() {
        return masterVariables;
    }

    public Collection<String> getDependentVariables() {
        return dependentVariables;
    }

    public Collection<VariableDefinition> getInputVariables() {
        if (inputVariables == null) {
            buildInputVariables();
        }
        return inputVariables;
    }

    @Override
    public String toString() {
        ToStringBuilder tos = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        tos.append("usedVariables", usedVariables);
        tos.append("unusedVariables", unusedVariables);
        tos.append("undefinedVariables", undefinedVariables);
        tos.append("masterVariables", masterVariables);
        tos.append("dependentVariables", dependentVariables);
        tos.append("conditionals", conditionals);
        return tos.toString();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " isn't cloneable: " + ex.getMessage(), ex);
        }
    }

    private void buildInputVariables() {
        this.inputVariables = new ArrayList<>();
        TemplateDefinition templateDefinition = template.getTemplateDefinition();
        Set<String> variablesSet = new HashSet<>();
        if (templateDefinition != null) {
            // At first add all definined variables in their order, if used.
            for (VariableDefinition variableDefinition : templateDefinition.getVariableDefinitions()) {
                if (usedVariables.contains(variableDefinition.getName())) {
                    inputVariables.add(variableDefinition);
                    variablesSet.add(variableDefinition.getName());
                    continue;
                }
                // Check if this is a master variable and any used variable depends on:
                if (containsDependentVariable(templateDefinition, variableDefinition.getName())) {
                    inputVariables.add(variableDefinition);
                    variablesSet.add(variableDefinition.getName());
                }
            }
        }
        // Now, check for additional undefined but used variables:
        for (String variable : usedVariables) {
            if (!variablesSet.contains(variable)) {
                if (isDependentVariable(templateDefinition, variable))
                    // Don't use dependent variables as input variables.
                    continue;
                inputVariables.add(new VariableDefinition(variable)); // Undefined variable.
                variablesSet.add(variable);
            }
        }
    }

    private boolean containsDependentVariable(TemplateDefinition templateDefinition, String variable) {
        for (DependentVariableDefinition dependentVariableDefinition : templateDefinition.getDependentVariableDefinitions()) {
            if (dependentVariableDefinition.getDependsOn().getName().equals(variable)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDependentVariable(TemplateDefinition templateDefinition, String variable) {
        if (templateDefinition == null) {
            return false;
        }
        for (DependentVariableDefinition dependentVariableDefinition : templateDefinition.getDependentVariableDefinitions()) {
            if (dependentVariableDefinition.getName().equals(variable)) {
                return true;
            }
        }
        return false;
    }
}
