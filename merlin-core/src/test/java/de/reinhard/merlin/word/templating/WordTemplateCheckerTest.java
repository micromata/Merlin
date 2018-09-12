package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.word.WordDocument;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WordTemplateCheckerTest {
    private Logger log = LoggerFactory.getLogger(WordTemplateCheckerTest.class);

    @Test
    public void readWordTest() throws Exception {
        TemplateDefinition templateDefinition = DefinitionExcelConverterTest.create();
        WordDocument templateDocument = new WordDocument(new File(Definitions.EXAMPLES_TEST_DIR, "ContractTemplate.docx"));
        WordTemplateChecker checker = new WordTemplateChecker(templateDefinition, templateDocument);
    }
}
