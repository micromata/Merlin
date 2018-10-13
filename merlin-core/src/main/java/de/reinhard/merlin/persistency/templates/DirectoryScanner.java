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

    private PersistencyInterface persistency = PersistencyRegistry.getDefault();
    private AbstractDirectoryWatcher directoryWatcher;
    private TemplatesHandler templatesHandler;
    private TemplateDefinitionsHandler templateDefinitionsHandler;
    private SerialDatasHandler serialDatasHandler;

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
        return templatesHandler.getItems();
    }

    public Template getTemplate(String primaryKey) {
        checkAndRefreshAllItems();
        return templatesHandler.getItem(primaryKey);
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
     * @param id Id or name of the template definition to search for.
     * @return
     */
    public TemplateDefinition getTemplateDefinition(String id) {
        if (id == null) {
            return null;
        }
        templateDefinitionsHandler.checkAndRefreshItems();
        return templateDefinitionsHandler.getTemplateDefinition(id);
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

    private void checkAndRefreshAllItems() {
        templateDefinitionsHandler.checkAndRefreshItems();
        templatesHandler.checkAndRefreshItems();
    }

    public AbstractDirectoryWatcher getDirectoryWatcher() {
        return directoryWatcher;
    }
}
