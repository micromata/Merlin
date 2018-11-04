package de.micromata.merlin.app.storage;

import de.reinhard.merlin.persistency.templates.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Creates test data.
 */
public class TestData {
    private static Logger log = LoggerFactory.getLogger(TestData.class);

    private static final String TEST_TEMPLATES_DIR = "examples/templates";
    private static DirectoryScanner testDirectoryScanner;

    public static DirectoryScanner getTestDirectory(File parent) {
        if (testDirectoryScanner != null) {
            return testDirectoryScanner;
        }
        File dir = new File(parent, TEST_TEMPLATES_DIR).getAbsoluteFile();
        log.info("Creating test data from '" + dir.toPath());
        testDirectoryScanner = new DirectoryScanner(dir.toPath(), true);
        if (testDirectoryScanner.getTemplateDefinitions() == null) {
            log.error("Can't scan directory '" + dir.getAbsolutePath() + "' for test template files.");
            return null;
        }
        return testDirectoryScanner;
    }
}
