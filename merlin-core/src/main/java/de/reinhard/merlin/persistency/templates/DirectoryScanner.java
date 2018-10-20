package de.reinhard.merlin.persistency.templates;

import de.reinhard.merlin.persistency.AbstractDirectoryWatcher;
import de.reinhard.merlin.persistency.FileDescriptor;
import de.reinhard.merlin.persistency.PersistencyInterface;
import de.reinhard.merlin.persistency.PersistencyRegistry;
import de.reinhard.merlin.word.templating.SerialData;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Searches in directories for Merlin template definition files and template files.
 */
public class DirectoryScanner {
    private Logger log = LoggerFactory.getLogger(DirectoryScanner.class);
    private static final int MAX_REFRESH_RATE_MILLIS = 5000; // Refresh only every 5 seconds.

    private PersistencyInterface persistency = PersistencyRegistry.getDefault();
    private AbstractDirectoryWatcher directoryWatcher;
    private TemplatesHandler templatesHandler;
    private TemplateDefinitionsHandler templateDefinitionsHandler;
    private SerialDatasHandler serialDatasHandler;
    private long lastRefresh = -1;

    /**
     * @param dir
     * @param recursive If true, the directory will be searched recursively for Merlin templates. Default is false.
     */
    public DirectoryScanner(Path dir, boolean recursive) {
        dir = persistency.getCanonicalPath(dir);
        directoryWatcher = PersistencyRegistry.getInstance().getPersistency().newInstance(dir, recursive, "docx", "xls", "xlsx");
        directoryWatcher.setIgnoreFilenamePatterns("^~\\$.*");
        templatesHandler = new TemplatesHandler(this);
        templateDefinitionsHandler = new TemplateDefinitionsHandler(this);
        serialDatasHandler = new SerialDatasHandler(this);
        clear();
    }

    /**
     * Checks multiple occurrences of template id's.
     * TODO: Return statistics object.
     */
    public void check() {
        checkAndRefreshAllItems();
        Set<String> templateIdsSet = new HashSet<>();
        for (TemplateDefinition definition : templateDefinitionsHandler.getItems()) {
            String id = StringUtils.trim(definition.getId());
            if (StringUtils.isBlank(definition.getId())) {
                log.warn("Template definition id is blank: " + definition.getFileDescriptor());
            } else if (templateIdsSet.contains(id)) {
                log.warn("Multiple template definition id's found '" + definition.getId() + "': " + definition.getFileDescriptor());
            } else {
                templateIdsSet.add(id);
            }
        }
    }

    public String getCanonicalPath() {
        return this.directoryWatcher.getRootDir().toString();
    }

    /**
     * Deletes all content and reforce a full reload.
     */
    public void clear() {
        templateDefinitionsHandler.clear();
        templatesHandler.clear();
        serialDatasHandler.clear();
        directoryWatcher.clear();
    }

    public Collection<Template> getTemplates() {
        checkAndRefreshAllItems();
        Collection<Template> templates = templatesHandler.getItems();
        if (templates != null) {
            for (Template template : templates) {
                updateTemplate(template);
            }
        }
        return templates;
    }

    public Template getTemplate(String primaryKey) {
        checkAndRefreshAllItems();
        Template template = templatesHandler.getItem(primaryKey);
        updateTemplate(template);
        return template;
    }

    private void updateTemplate(Template template) {
        if (template == null) {
            return;
        }
        TemplateDefinition templateDefinition = template.getTemplateDefinition();
        if (templateDefinition != null) {
            // Check updated template definition:
            TemplateDefinition newTemplateDefinition = templateDefinitionsHandler.getItem(templateDefinition.getPrimaryKey());
            if (templateDefinition != newTemplateDefinition) {
                // Template definition was reread:
                template.assignTemplateDefinition(newTemplateDefinition);
            }
        }
    }

    public Collection<TemplateDefinition> getTemplateDefinitions() {
        templateDefinitionsHandler.checkAndRefreshItems();
        return templateDefinitionsHandler.getItems();
    }

    public TemplateDefinition getTemplateDefinition(FileDescriptor descriptor) {
        templateDefinitionsHandler.checkAndRefreshItems();
        return templateDefinitionsHandler.getItem(descriptor);
    }

    /**
     * @param idOrPrimaryKey Id or primary key of the template definition to search for.
     * @return
     */
    public TemplateDefinition getTemplateDefinition(String idOrPrimaryKey) {
        if (idOrPrimaryKey == null) {
            return null;
        }
        templateDefinitionsHandler.checkAndRefreshItems();
        return templateDefinitionsHandler.getTemplateDefinition(idOrPrimaryKey);
    }

    TemplateDefinitionsHandler getTemplateDefinitionsHandler() {
        return templateDefinitionsHandler;
    }

    public Collection<SerialData> getSerialDatas() {
        checkAndRefreshAllItems();
        return serialDatasHandler.getItems();
    }

    public SerialData getSerialData(String primaryKey) {
        checkAndRefreshAllItems();
        return serialDatasHandler.getItem(primaryKey);
    }


    public Path getDir() {
        return directoryWatcher.getRootDir();
    }

    private synchronized void checkAndRefreshAllItems() {
        long now = System.currentTimeMillis();
        if (now < lastRefresh + MAX_REFRESH_RATE_MILLIS) {
            return;
        }
        templateDefinitionsHandler.checkAndRefreshItems();
        templatesHandler.checkAndRefreshItems();
        lastRefresh = now;
    }

    public AbstractDirectoryWatcher getDirectoryWatcher() {
        return directoryWatcher;
    }
}
