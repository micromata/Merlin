package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.persistency.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Creates test data.
 */
public class TestData {
    private static Logger log = LoggerFactory.getLogger(TestData.class);

    private static final String TEST_TEMPLATES_DIR = "examples/templates";
    private static boolean created;

    public static void create(File parent) {
        if (created) {
            return; // Don't create twice.
        }
        created = true;
        File dir = new File(parent, TEST_TEMPLATES_DIR).getAbsoluteFile();
        log.info("Creating test data from '" + dir.toPath());
        DirectoryScanner directoryScanner = new DirectoryScanner(dir.toPath(), false);
        if (directoryScanner.getTemplateDefinitions() == null) {
            log.error("Can't scan directory '" + dir.getAbsolutePath() + "' for test template files.");
            return;
        }
        Storage.getInstance().add(directoryScanner);
    }
}
