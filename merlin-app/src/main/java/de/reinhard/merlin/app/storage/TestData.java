package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.word.templating.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Creates test data.
 */
public class TestData {
    private static Logger log = LoggerFactory.getLogger(TestData.class);

    private static final String TEST_TEMPLATES_DIR = "examples/templates";

    public static void create(File parent) {
        File dir = new File(parent, TEST_TEMPLATES_DIR);
        DirectoryScanner directoryScanner = new DirectoryScanner(dir.toPath(), false);
        directoryScanner.process();
        if (directoryScanner.getTemplateDefinitions() == null) {
            log.error("Can't scan directory '" + dir.getAbsolutePath() + "' for test template files.");
            return;
        }
        Storage.getInstance().add(directoryScanner);
    }
}
