package de.micromata.merlin.persistency;

import de.micromata.merlin.Definitions;
import de.micromata.merlin.persistency.templates.DirectoryScanner;
import de.micromata.merlin.word.templating.Template;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DirectoryScannerTest {
    @Test
    void scanTest() {
        DirectoryScanner directoryScanner = new DirectoryScanner(Definitions.EXAMPLES_TEMPLATES_TEST_DIR.toPath(), false);
        assertEquals(2, directoryScanner.getTemplateDefinitions().size());
        assertEquals("LetterTemplate.xlsx",
                directoryScanner.getTemplateDefinition("Letter-Definition").getFileDescriptor().getFilename());
        assertEquals("EmploymentContractTemplate.xlsx",
                directoryScanner.getTemplateDefinition("Employment contract definition").getFileDescriptor().getFilename());
        assertEquals(3, directoryScanner.getTemplates().size());
        File templateFile = new File(Definitions.EXAMPLES_TEMPLATES_TEST_DIR, "LetterTemplate.docx");
        String templatePath = PersistencyRegistry.getDefault().getPrimaryKey(templateFile.toPath());
        Template template = directoryScanner.getTemplate(templatePath);
        assertNotNull(template);
        assertNotNull(template.getTemplateDefinition());
        assertEquals("Letter-Definition", template.getTemplateDefinitionId());
    }
}
