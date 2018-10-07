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
    private Map<Path, TemplateDefinition> templateDefinitionsMap = new HashMap<>();
    private Map<Path, Template> templatesMap = new HashMap<>();

    /**
     * @param dir
     * @param recursive If true, the directory will be searched recursively for Merlin templates. Default is false.
     */
    public DirectoryScanner(Path dir, boolean recursive) {
        dir = persistency.getCanonicalPath(dir);
        directoryWatcher = PersistencyRegistry.getInstance().getPersistency().newInstance(dir, recursive, "docx", "xls", "xlsx");
        directoryWatcher.setIgnoreFilenamePatterns("^~\\$.*");
        clear();
    }

    /**
     * Checks multiple occurrences of template id's.
     * TODO: Return statistics object.
     */
    public void check() {
        checkAll();
        Set<String> templateIdsSet = new HashSet<>();
        for (TemplateDefinition definition : this.templateDefinitionsMap.values()) {
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
        templateDefinitionsMap.clear();
        templatesMap.clear();
        directoryWatcher.clear();
    }

    public Template getTemplate(FileDescriptor descriptor) {
        return getTemplate(descriptor.getCanonicalPathString());
    }

    Template _getTemplate(FileDescriptor descriptor) {
        return _getTemplate(descriptor.getCanonicalPathString());
    }

    public Template getTemplate(String canonicalPath) {
        checkAll();
        return _getTemplate(canonicalPath);
    }

    private Template _getTemplate(String canonicalPath) {
        for (Template template : templatesMap.values()) {
            if (canonicalPath.equals(template.getFileDescriptor().getCanonicalPathString())) {
                return template;
            }
        }
        return null;
    }

    /**
     * Gets the template definition by path. If item or file is updated since last update, the template definition file
     * will be re-read.
     *
     * @param watchEntry
     * @return Found or read template definition, otherwise null.
     */
    protected TemplateDefinition getTemplateDefinition(DirectoryWatchEntry watchEntry) {
        Path path = directoryWatcher.getCanonicalPath(watchEntry);
        TemplateDefinitionHandler handler = new TemplateDefinitionHandler(this);
        TemplateDefinition templateDefinition = handler.get(watchEntry);
        if (templateDefinition != null) {
            templateDefinitionsMap.put(path, templateDefinition);
        }
        return templateDefinition;
    }


    protected Template getTemplate(DirectoryWatchEntry watchEntry) {
        Path path = directoryWatcher.getCanonicalPath(watchEntry);
        TemplateHandler handler = new TemplateHandler(this);
        Template template = handler.get(watchEntry);
        if (template != null) {
            templatesMap.put(path, template);
        }
        return template;
    }

    public Collection<TemplateDefinition> getTemplateDefinitions() {
        checkTemplateDefinitions();
        return templateDefinitionsMap.values();
    }


    public TemplateDefinition getTemplateDefinition(FileDescriptor descriptor) {
        checkTemplateDefinitions();
        return _getTemplateDefinition(descriptor);
    }

    TemplateDefinition _getTemplateDefinition(FileDescriptor descriptor) {
        for (TemplateDefinition templateDefinition : templateDefinitionsMap.values()) {
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
        checkTemplateDefinitions();
        return _getTemplateDefinition(id);
    }

    TemplateDefinition _getTemplateDefinition(String id) {
        String search = id.trim().toLowerCase();
        for (TemplateDefinition templateDefinition : templateDefinitionsMap.values()) {
            if (search.equals(templateDefinition.getId().trim().toLowerCase())) {
                return templateDefinition;
            }
        }
        return null;
    }

    public Collection<Template> getTemplates() {
        checkAll();
        return templatesMap.values();
    }


    public Path getDir() {
        return directoryWatcher.getRootDir();
    }

    private void checkTemplateDefinitions() {
        // Check for new, deleted and updated files:
        List<DirectoryWatchEntry> watchEntries = directoryWatcher.listWatchEntries(true, "xlsx", "xls");
        for (DirectoryWatchEntry watchEntry : watchEntries) {
            TemplateDefinition templateDefinition = templateDefinitionsMap.get(directoryWatcher.getCanonicalPath(watchEntry));
            if (templateDefinition == null) {
                log.debug("Creating new template definition: " + directoryWatcher.getCanonicalPath(watchEntry));
                templateDefinition = getTemplateDefinition(watchEntry);
            }
        }
        Iterator<Map.Entry<Path, TemplateDefinition>> it = templateDefinitionsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Path, TemplateDefinition> entry = it.next();
            if (directoryWatcher.isDeleted(entry.getValue().getFileDescriptor().getCanonicalPath())) {
                log.debug("Remove deleted template definition '" + entry.getValue().getId() + "': " + entry.getKey());
                templateDefinitionsMap.remove(entry.getKey());
            }
        }
    }

    private void checkAll() {
        checkTemplateDefinitions(); // Templates reference definitions.
        // Check for new, deleted and updated files:
        List<DirectoryWatchEntry> watchEntries = directoryWatcher.listWatchEntries(true, "docx");
        for (DirectoryWatchEntry watchEntry : watchEntries) {
            Template template = templatesMap.get(directoryWatcher.getCanonicalPath(watchEntry));
            if (template == null) {
                log.debug("Creating new template: " + directoryWatcher.getCanonicalPath(watchEntry));
                template = getTemplate(watchEntry);
            }
        }
        Iterator<Map.Entry<Path, Template>> it = templatesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Path, Template> entry = it.next();
            if (directoryWatcher.isDeleted(entry.getValue().getFileDescriptor().getCanonicalPath())) {
                log.debug("Remove deleted template: " + entry.getKey());
                templatesMap.remove(entry.getKey());
            }
        }
    }

    public AbstractDirectoryWatcher getDirectoryWatcher() {
        return directoryWatcher;
    }
}
