package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.word.Conditionals;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Some statistics for templates
 */
public class TemplateStatistics {
    private Logger log = LoggerFactory.getLogger(TemplateStatistics.class);
    private List<String> usedVariables = new ArrayList<>();
    private Conditionals conditionals;
    private Template template;
    private Collection<String> allDefinedVariables; // All variables defined in TemplateDefinition.
    private List<String> allUsedVariables;          // All variables used in the Word template.
    private Collection<String> unusedVariables;     // Variables defined but not used in the Word template.
    private Collection<String> undefinedVariables;  // Variables used in the Word template but not defined.

    public List<String> getUsedVariables() {
        return usedVariables;
    }

    public void setUsedVariables(List<String> usedVariables) {
        this.usedVariables = usedVariables;
    }

    public TemplateStatistics(Template parent) {
        this.template = parent;
    }

    /**
     * Analyzes used variables by this template and compares it to the defined variables in the given templateDefinition.
     */
    public void updateStatistics() {
        if (template.getTemplateDefinition() == null) {
            log.debug("No templateDefinition given. Can't update statistics. Clearing statistics.");
            this.allDefinedVariables = null;
            this.unusedVariables = null;
            this.undefinedVariables = null;
            return;
        }
        this.allDefinedVariables = template.getTemplateDefinition().getAllDefinedVariableNames();
        if (log.isDebugEnabled()) {
            for (String variable : this.allDefinedVariables) {
                log.debug("Defined variable: " + variable);
            }
        }
        this.unusedVariables = CollectionUtils.subtract(allDefinedVariables, allUsedVariables);
        if (log.isDebugEnabled()) {
            for (String variable : unusedVariables) {
                log.debug("Unused: " + variable);
            }
        }
        this.undefinedVariables = CollectionUtils.subtract(allUsedVariables, allDefinedVariables);
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

    public List<String> getAllUsedVariables() {
        return allUsedVariables;
    }

    public void setAllUsedVariables(List<String> allUsedVariables) {
        this.allUsedVariables = allUsedVariables;
    }

    public Collection<String> getUnusedVariables() {
        return unusedVariables;
    }

    public void setUnusedVariables(Collection<String> unusedVariables) {
        this.unusedVariables = unusedVariables;
    }

    public Collection<String> getUndefinedVariables() {
        return undefinedVariables;
    }

    public void setUndefinedVariables(Collection<String> undefinedVariables) {
        this.undefinedVariables = undefinedVariables;
    }

    @Override
    public String toString() {
        ToStringBuilder tos = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        tos.append("allUsedVariables", allUsedVariables);
        tos.append("usedVariables", usedVariables);
        tos.append("unusedVariables", unusedVariables);
        tos.append("undefinedVariables", undefinedVariables);
        tos.append("conditionals", conditionals);
        return tos.toString();
    }
}
