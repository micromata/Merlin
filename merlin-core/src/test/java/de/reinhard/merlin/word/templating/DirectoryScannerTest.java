package de.reinhard.merlin.word.templating;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectoryScannerTest {
    private static final String TEST_TEMPLATES_DIR = "../examples/templates/";

    @Test
    public void scanTest() {
        DirectoryScanner directoryScanner = new DirectoryScanner(new File(TEST_TEMPLATES_DIR), false);
        directoryScanner.process();
        assertEquals(2, directoryScanner.getTemplateDefinitions().size());
        assertEquals("9MJdzFN2v2PKMJ9erj59", directoryScanner.getTemplateDefinitions().get(0).getId());
        assertEquals("JZpnpojeSuN5JDqtm9KZ", directoryScanner.getTemplateDefinitions().get(1).getId());
        assertEquals("LetterTemplate.xlsx",
                directoryScanner.getTemplateDefinition("9MJdzFN2v2PKMJ9erj59").getFileDescriptor().getFilename());
        assertEquals("ContractTemplate.xlsx",
                directoryScanner.getTemplateDefinition("JZpnpojeSuN5JDqtm9KZ").getFileDescriptor().getFilename());
    }
}
