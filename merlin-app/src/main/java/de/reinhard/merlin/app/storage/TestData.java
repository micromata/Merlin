package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.word.templating.DirectoryScanner;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Creates test data.
 */
public class TestData {
    private static Logger log = LoggerFactory.getLogger(TestData.class);

    private static final String TEST_TEMPLATES_DIR = "merlin-app/test/templates/";

    public static void create(File parent) {
        DirectoryScanner directoryScanner = new DirectoryScanner();
        File dir = new File(parent, TEST_TEMPLATES_DIR);
        directoryScanner.process(dir);
        if (directoryScanner.getTemplates() == null) {
            log.error("Can't scan directory '" + dir.getAbsolutePath() + "' for test template files.");
            return;
        }
        for (TemplateDefinition templateDefinition : directoryScanner.getTemplates()) {
            Storage.getInstance().add(templateDefinition, directoryScanner.getTemplateFile(templateDefinition.getId()));
        }
    }
}
