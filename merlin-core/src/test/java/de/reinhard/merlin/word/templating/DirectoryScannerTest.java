package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.Definitions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectoryScannerTest {
    @Test
    public void scanTest() {
        DirectoryScanner directoryScanner = new DirectoryScanner(Definitions.EXAMPLES_TEMPLATES_TEST_DIR.toPath(), false);
        assertEquals(2, directoryScanner.getTemplateDefinitions().size());
        assertEquals("LetterTemplate.xlsx",
                directoryScanner.getTemplateDefinition("Letter-Template").getFileDescriptor().getFilename());
        assertEquals("EmploymentContractTemplate.xlsx",
                directoryScanner.getTemplateDefinition("Employment contract template").getFileDescriptor().getFilename());
    }
}
