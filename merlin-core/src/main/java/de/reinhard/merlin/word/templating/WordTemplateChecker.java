package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.word.AbstractConditional;
import de.reinhard.merlin.word.WordDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Scans a template for used variables.
 */
public class WordTemplateChecker {
    private Logger log = LoggerFactory.getLogger(WordTemplateChecker.class);
    private Template template;
    private WordDocument document;

    public WordTemplateChecker(WordDocument document) {
        this.document = document;
        Set<String> variables = document.getVariables();
        template = new Template();
        TemplateStatistics statistics = template.getStatistics();
        statistics.setConditionals(document.getConditionals());
        if (statistics.getConditionals() != null) {
            for (AbstractConditional conditional : statistics.getConditionals().getConditionalsSet()) {
                variables.add(conditional.getVariable());
            }
        }
        statistics.setAllUsedVariables(new ArrayList<>());
        statistics.getAllUsedVariables().addAll(variables);
        statistics.getUsedVariables().addAll(variables);
        Collections.sort(statistics.getUsedVariables(), String.CASE_INSENSITIVE_ORDER);
        Collections.sort(statistics.getAllUsedVariables(), String.CASE_INSENSITIVE_ORDER);
        if (log.isDebugEnabled()) {
            for (String variable : statistics.getAllUsedVariables()) {
                log.debug("Used variable: " + variable);
            }
        }
    }

    /**
     * Sets the template definition and analyzed used, defined and unused variables.
     *
     * @param templateDefinition
     */
    public void assignTemplateDefinition(TemplateDefinition templateDefinition) {
        template.assignTemplateDefinition(templateDefinition);
    }

    public Template getTemplate() {
        return template;
    }
}
