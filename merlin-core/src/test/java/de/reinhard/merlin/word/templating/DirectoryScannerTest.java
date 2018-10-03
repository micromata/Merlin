package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.Definitions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectoryScannerTest {
    @Test
    public void scanTest() {
        DirectoryScanner directoryScanner = new DirectoryScanner(Definitions.EXAMPLES_TEMPLATES_TEST_DIR, false);
        directoryScanner.process();
        assertEquals(2, directoryScanner.getTemplateDefinitions().size());
        assertEquals("9MJdzFN2v2PKMJ9erj59", directoryScanner.getTemplateDefinitions().get(0).getId());
        assertEquals("JZpnpojeSuN5JDqtm9KZ", directoryScanner.getTemplateDefinitions().get(1).getId());
        assertEquals("LetterTemplate.xlsx",
                directoryScanner.getTemplateDefinition("9MJdzFN2v2PKMJ9erj59").getFileDescriptor().getFilename());
        assertEquals("EmploymentContractTemplate.xlsx",
                directoryScanner.getTemplateDefinition("JZpnpojeSuN5JDqtm9KZ").getFileDescriptor().getFilename());
    }
}
