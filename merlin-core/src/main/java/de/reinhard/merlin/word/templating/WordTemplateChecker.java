package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.word.AbstractConditional;
import de.reinhard.merlin.word.Conditionals;
import de.reinhard.merlin.word.WordDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Scans a template for used variables.
 */
public class WordTemplateChecker {
    private Logger log = LoggerFactory.getLogger(WordTemplateChecker.class);
    private TemplateDefinition templateDefinition;
    private WordDocument document;
    private Conditionals conditionals;
    private Collection<String> allDefinedVariables; // All variables defined in TemplateDefinition.
    private List<String> allUsedVariables;          // All variables used in the Word template.
    private Collection<String> unusedVariables;     // Variables defined but not used in the Word template.
    private Collection<String> undefinedVariables;  // Variables used in the Word template but not defined.

    public WordTemplateChecker(TemplateDefinition templateDefinition, WordDocument document) {
        this.templateDefinition = templateDefinition;
        this.document = document;
        Set<String> variables = document.getVariables();
        this.conditionals = document.getConditionals();
        if (conditionals.getConditionals() != null) {
            for (AbstractConditional conditional : conditionals.getConditionals()) {
                variables.add(conditional.getVariable());
            }
        }
        this.allUsedVariables = new ArrayList<>();
        this.allUsedVariables.addAll(variables);
        Collections.sort(this.allUsedVariables, String.CASE_INSENSITIVE_ORDER);
        if (log.isDebugEnabled()) {
            for (String variable : this.allUsedVariables) {
                log.debug("Used variable: " + variable);
            }
        }

        this.allDefinedVariables = templateDefinition.getAllDefinedVariableNames();
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
}
