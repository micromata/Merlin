package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.word.AbstractConditional;
import de.reinhard.merlin.word.Conditionals;
import de.reinhard.merlin.word.WordDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Scans a template for used variables.
 */
public class WordTemplateChecker {
    private Logger log = LoggerFactory.getLogger(WordTemplateChecker.class);
    private TemplateDefinition templateDefinition;
    private WordDocument document;
    private Conditionals conditionals;
    private List<String> variables;

    public WordTemplateChecker(TemplateDefinition templateDefinition, WordDocument document) {
        this.templateDefinition = templateDefinition;
        this.document = document;
        Set<String> vars = document.getVariables();
        this.conditionals = document.getConditionals();
        if (conditionals.getConditionals() != null) {
            for (AbstractConditional conditional : conditionals.getConditionals()) {
                vars.add(conditional.getVariable());
            }
        }
        this.variables = new LinkedList<>();
        this.variables.addAll(vars);
        Collections.sort(this.variables, String.CASE_INSENSITIVE_ORDER);
        if (log.isDebugEnabled()) {
            for (String variable : this.variables) {
                log.debug("Variable: " + variable);
            }
        }
    }
}
