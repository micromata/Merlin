package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.word.WordDocument;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Runs a template.
 */
public class WordTemplateRunner {
    private Logger log = LoggerFactory.getLogger(WordTemplateRunner.class);
    private TemplateDefinition templateDefinition;
    private WordDocument srcDocument;

    public WordTemplateRunner(TemplateDefinition templateDefinition, WordDocument document) {
        this.templateDefinition = templateDefinition;
        this.srcDocument = document;
    }

    public WordDocument run(Map<String, Object> variables) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            srcDocument.getDocument().write(bos);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        WordDocument newDocument = new WordDocument(bos.toInputStream());
        newDocument.setFilename(srcDocument.getFilename());
        if (templateDefinition.getDependentVariableDefinitions() != null) {
            for (DependentVariableDefinition depVar : templateDefinition.getDependentVariableDefinitions()) {
                variables.put(depVar.getName(), depVar.getMappedValue(variables));
            }
        }
        newDocument.process(variables);
        return newDocument;
    }
}
