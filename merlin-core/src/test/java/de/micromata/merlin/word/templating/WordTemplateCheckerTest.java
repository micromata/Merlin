package de.micromata.merlin.word.templating;

import de.micromata.merlin.Definitions;
import de.micromata.merlin.word.WordDocument;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WordTemplateCheckerTest {
    private Logger log = LoggerFactory.getLogger(WordTemplateCheckerTest.class);

    @Test
    public void readWordTest() throws Exception {
        TemplateDefinition templateDefinition = DefinitionExcelConverterTest.create();
        WordDocument templateDocument = new WordDocument(new File(Definitions.EXAMPLES_TEMPLATES_TEST_DIR, "EmploymentContractTemplate.docx"));
        VariableDefinition variableDefinition = templateDefinition.getVariableDefinitions().get(0);
        variableDefinition.setName("New_variable");
        WordTemplateChecker checker = new WordTemplateChecker(templateDocument);
        checker.assignTemplateDefinition(templateDefinition);
    }
}
