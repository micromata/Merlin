package de.reinhard.merlin.persistency.templates;

import de.reinhard.merlin.persistency.*;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * Searches in directories for Merlin template definition files and template files.
 */
public class DirectoryScanner {
    private Logger log = LoggerFactory.getLogger(DirectoryScanner.class);

    private PersistencyInterface persistency = PersistencyRegistry.getDefault();
    private AbstractDirectoryWatcher directoryWatcher;
    private TemplatesHandler templatesHandler;
    private TemplateDefinitionsHandler templateDefinitionsHandler;

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
        templateDefinitionsHandler.clear();
        directoryWatcher.clear();
    }

    public Collection<Template> getTemplates() {
        checkAndRefreshAllItems();
        return templatesHandler.getItems();
    }

    public Template getTemplate(FileDescriptor descriptor) {
        return getTemplate(descriptor.getCanonicalPathString());
    }

    public Template getTemplate(String canonicalPath) {
        checkAndRefreshAllItems();
        return _getTemplate(canonicalPath);
    }

    Template _getTemplate(FileDescriptor descriptor) {
        return _getTemplate(descriptor.getCanonicalPathString());
    }

    private Template _getTemplate(String canonicalPath) {
        for (Template template : templatesHandler.getItems()) {
            if (canonicalPath.equals(template.getFileDescriptor().getCanonicalPathString())) {
                return template;
            }
        }
        return null;
    }

    public Collection<TemplateDefinition> getTemplateDefinitions() {
        templateDefinitionsHandler.checkAndRefreshItems();
        return templateDefinitionsHandler.getItems();
    }
    
    public TemplateDefinition getTemplateDefinition(FileDescriptor descriptor) {
        templateDefinitionsHandler.checkAndRefreshItems();
        return _getTemplateDefinition(descriptor);
    }

    TemplateDefinition _getTemplateDefinition(FileDescriptor descriptor) {
        for (TemplateDefinition templateDefinition : templateDefinitionsHandler.getItems()) {
            if (descriptor.equals(templateDefinition.getFileDescriptor())) {
                return templateDefinition;
            }
        }
        return null;
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
        return _getTemplateDefinition(id);
    }

    TemplateDefinition _getTemplateDefinition(String id) {
        String search = id.trim().toLowerCase();
        for (TemplateDefinition templateDefinition : templateDefinitionsHandler.getItems()) {
            if (search.equals(templateDefinition.getId().trim().toLowerCase())) {
                return templateDefinition;
            }
        }
        return null;
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
