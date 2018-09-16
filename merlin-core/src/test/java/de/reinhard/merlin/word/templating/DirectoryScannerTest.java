package de.reinhard.merlin.word.templating;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectoryScannerTest {
    private static final String TEST_TEMPLATES_DIR = "../merlin-app/test/templates/";

    @Test
    public void scanTest() {
        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.process(new File(TEST_TEMPLATES_DIR));
        assertEquals(3, directoryScanner.getTemplates().size());
        assertEquals("9MJdzFN2v2PKMJ9erj59", directoryScanner.getTemplates().get(0).getId());
        assertEquals("hDl7LBuOJ1kzqF09gUHP", directoryScanner.getTemplates().get(1).getId());
        assertEquals("JZpnpojeSuN5JDqtm9KZ", directoryScanner.getTemplates().get(2).getId());
        assertEquals("../merlin-app/test/templates/LetterTemplate.xlsx",
                directoryScanner.getTemplateFile("9MJdzFN2v2PKMJ9erj59").toString());
        assertEquals("../merlin-app/test/templates/LetterTemplate-old.xls",
                directoryScanner.getTemplateFile("hDl7LBuOJ1kzqF09gUHP").toString());
        assertEquals("../merlin-app/test/templates/ContractTemplate.xlsx",
                directoryScanner.getTemplateFile("JZpnpojeSuN5JDqtm9KZ").toString());
    }
}
