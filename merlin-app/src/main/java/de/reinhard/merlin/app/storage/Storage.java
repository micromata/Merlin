package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.app.ConfigurationHandler;
import de.reinhard.merlin.app.ConfigurationListener;
import de.reinhard.merlin.app.ConfigurationTemplatesDir;
import de.reinhard.merlin.app.javafx.RunningMode;
import de.reinhard.merlin.persistency.templates.DirectoryScanner;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage implements ConfigurationListener {
    private Logger log = LoggerFactory.getLogger(Storage.class);
    private static final Storage instance = new Storage();

    // Key is the canonical path of the directory.
    private Map<String, DirectoryScanner> directoryScannerMap;

    private boolean dirty = true;

    public static Storage getInstance() {
        return instance;
    }

    private Storage() {
        directoryScannerMap = new HashMap<>();
        ConfigurationHandler.getInstance().register(this);
    }

    /**
     * Will be called on start-up of the server. The templates should be scanned already (takes some time).
     */
    public void onStartup() {
        getAllTemplates();
    }

    public void add(DirectoryScanner directoryScanner) {
        directoryScannerMap.put(directoryScanner.getCanonicalPath(), directoryScanner);
    }

    public List<TemplateDefinition> getAllTemplateDefinitions() {
        checkRefresh();
        List<TemplateDefinition> templateDefinitions = new ArrayList<>();
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            templateDefinitions.addAll(directoryScanner.getTemplateDefinitions());
        }
        return templateDefinitions;
    }

    /**
     * @param idOrPrimaryKey Id or primary key of the template definition to return.
     * @return
     */
    public TemplateDefinition getTemplateDefinition(String idOrPrimaryKey) {
        Validate.notNull(idOrPrimaryKey);
        checkRefresh();
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            TemplateDefinition templateDefinition = directoryScanner.getTemplateDefinition(idOrPrimaryKey);
            if (templateDefinition != null) {
                return templateDefinition;
            }
        }
        log.error("Template definition with id or primary key '" + idOrPrimaryKey + "' not found.");
        return null;
    }

    public List<Template> getAllTemplates() {
        checkRefresh();
        List<Template> templates = new ArrayList<>();
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            templates.addAll(directoryScanner.getTemplates());
        }
        return templates;
    }

    public Template getTemplate(String primaryKey) {
        Validate.notNull(primaryKey);
        checkRefresh();
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            Template template = directoryScanner.getTemplate(primaryKey);
            if (template != null) {
                return template;
            }
        }
        log.info("No template with primaryKey '" + primaryKey + "' found.");
        return null;
    }

    public synchronized void refresh() {
        log.info("(Re-)loading storage.");
        dirty = false;
        List<ConfigurationTemplatesDir> templatesDirs = ConfigurationHandler.getDefaultConfiguration().getTemplatesDirs();
        this.directoryScannerMap.clear();
        if (templatesDirs != null) {
            for (ConfigurationTemplatesDir configDir : templatesDirs) {
                DirectoryScanner scanner = new DirectoryScanner(Paths.get(configDir.getDirectory()), configDir.isRecursive());
                add(scanner);
            }
        }
        if (ConfigurationHandler.getDefaultConfiguration().isShowTestData()) {
            // Creating data for testing.
            add(TestData.getTestDirectory(RunningMode.getBaseDir()));
        }
    }

    private String normalizeTemplateId(String templateId) {
        return templateId != null ? templateId.trim().toLowerCase() : null;
    }

    private synchronized void checkRefresh() {
        if (!dirty) {
            return;
        }
        refresh();
    }

    /**
     * Forces refresh.
     */
    @Override
    public void templatesDirsModified() {
        dirty = true;
    }
}
