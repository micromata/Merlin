package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.persistency.AbstractDirectoryWatcher;
import de.reinhard.merlin.persistency.DirectoryWatchEntry;
import de.reinhard.merlin.persistency.PersistencyInterface;
import de.reinhard.merlin.persistency.PersistencyRegistry;
import de.reinhard.merlin.word.WordDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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

    private Template _getTemplate(FileDescriptor descriptor) {
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
        Date now = new Date();
        Path path = directoryWatcher.getCanonicalPath(watchEntry);
        FileDescriptor fileDescriptor = new FileDescriptor().setDirectory(getDir()).setRelativePath(path)
                .setLastUpdate(now);
        TemplateDefinition existingTemplateDefinition = _getTemplateDefinition(fileDescriptor);
        if (existingTemplateDefinition != null && !existingTemplateDefinition.getFileDescriptor().isModified(path)) {
            log.debug("Skipping file '" + path + "'. It's not modified since last scan.");
            return existingTemplateDefinition;
        }
        if (!persistency.exists(path)) {
            log.error("Can't read template definition. Path '" + path + "' doesn't exist.");
            return null;
        }
        if (!watchEntry.isSupportedItem() && !watchEntry.isModified(directoryWatcher)) {
            log.debug("Unsupported item '" + path + "' not modified. Skipping again.");
            return null;
        }
        log.info("Scanning file '" + path + "'.");
        ExcelWorkbook workbook = ExcelWorkbook.create(path);
        TemplateDefinitionExcelReader templateReader = new TemplateDefinitionExcelReader();
        if (!templateReader.isMerlinTemplateDefinition(workbook)) {
            log.info("Skipping file '" + path.toAbsolutePath() + "', no template definition (OK).");
            watchEntry.setSupportedItem(false);
            return null;
        }
        TemplateDefinition templateDefinition = templateReader.readFromWorkbook(workbook, false);
        if (templateDefinition == null) {
            log.info("Skipping file '" + path.toAbsolutePath() + "', no template definition (OK).");
            watchEntry.setSupportedItem(false);
            return null;
        }
        templateDefinition.setFileDescriptor(fileDescriptor);
        if (!templateReader.isValidTemplateDefinition()) {
            log.info("Skipping '" + path.toAbsolutePath() + "'. It's not a valid Merlin template file.");
            watchEntry.setSupportedItem(false);
            return null;
        }
        templateDefinitionsMap.put(path, templateDefinition);
        watchEntry.setSupportedItem(true);
        log.info("Valid Merlin template definition found: '" + templateDefinition.getId() + "': " + path.toAbsolutePath());
        return templateDefinition;
    }


    protected Template getTemplate(DirectoryWatchEntry watchEntry) {
        Date now = new Date();
        Path path = directoryWatcher.getCanonicalPath(watchEntry);
        FileDescriptor fileDescriptor = new FileDescriptor().setDirectory(getDir()).setRelativePath(path)
                .setLastUpdate(now);
        Template existingTemplate = _getTemplate(fileDescriptor);
        if (existingTemplate != null && !existingTemplate.getFileDescriptor().isModified(path)) {
            log.debug("Skipping file '" + path.toAbsolutePath() + "'. It's not modified since last scan.");
            return existingTemplate;
        }
        if (!persistency.exists(path)) {
            log.error("Can't read template. Path '" + path + "' doesn't exist.");
            return null;
        }
        if (!watchEntry.isSupportedItem() && !watchEntry.isModified(directoryWatcher)) {
            log.debug("Unsupported item '" + path + "' not modified. Skipping again.");
            return null;
        }
        log.info("Scanning file '" + path + "'.");
        WordDocument doc = WordDocument.create(path);
        WordTemplateChecker templateChecker = new WordTemplateChecker(doc);
        if (CollectionUtils.isEmpty(templateChecker.getTemplate().getStatistics().getAllUsedVariables())) {
            log.debug("Skipping Word document: '" + path.toAbsolutePath()
                    + "'. It's seemd to be not a Merlin template. No variables and conditionals found.");
            watchEntry.setSupportedItem(false);
            return null;
        }
        templateChecker.getTemplate().setFileDescriptor(fileDescriptor);
        String templateDefinitionId = doc.scanForTemplateDefinitionReference();
        if (templateDefinitionId != null) {
            log.debug("Template definition reference found: " + templateDefinitionId);
            TemplateDefinition templateDefinition = null;
            if (MapUtils.isEmpty(templateDefinitionsMap)) {
                log.warn("No templateDefinitions given, can't look for reference: " + templateDefinitionId);
            } else {
                String id = templateDefinitionId.trim().toLowerCase();
                for (TemplateDefinition def : templateDefinitionsMap.values()) {
                    if (id != null) {
                        if (id.equals(def.getId().trim().toLowerCase())) {
                            templateDefinition = def;
                            break;
                        }
                    }
                }
            }
            if (templateDefinition != null) {
                templateChecker.getTemplate().assignTemplateDefinition(templateDefinition);
            } else {
                log.warn("Template definition not found: " + templateDefinitionId);
            }
        } else {
            for (TemplateDefinition templateDefinition : templateDefinitionsMap.values()) {
                if (templateChecker.getTemplate().getFileDescriptor().matches(templateDefinition.getFileDescriptor())) {
                    templateChecker.getTemplate().assignTemplateDefinition(templateDefinition);
                    log.info("Found matching template definition: " + templateDefinition.getFileDescriptor());
                    templateChecker.getTemplate().assignTemplateDefinition(templateDefinition);
                    break;
                }
            }
        }
        templatesMap.put(path, templateChecker.getTemplate());
        watchEntry.setSupportedItem(true);
        log.info("Valid Merlin template found: '" + path.toAbsolutePath() + "'.");
        return templateChecker.getTemplate();
    }

    public Collection<TemplateDefinition> getTemplateDefinitions() {
        checkTemplateDefinitions();
        return templateDefinitionsMap.values();
    }


    public TemplateDefinition getTemplateDefinition(FileDescriptor descriptor) {
        checkTemplateDefinitions();
        return _getTemplateDefinition(descriptor);
    }

    private TemplateDefinition _getTemplateDefinition(FileDescriptor descriptor) {
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

}
