package de.reinhard.merlin.word.templating;

import org.junit.jupiter.api.Test;

import java.io.File;

public class DirectoryScannerTest {
    private static final String TEST_TEMPLATES_DIR = "../merlin-app/test/templates/";

    @Test
    public void scanTest() {
        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.process(new File(TEST_TEMPLATES_DIR));
    }
}
