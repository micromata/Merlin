package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.persistency.PersistencyInterface;
import de.reinhard.merlin.persistency.PersistencyRegistry;
import de.reinhard.merlin.persistency.DirectoryWatchService;
import de.reinhard.merlin.word.WordDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * Searches in directories for Merlin template definition files and template files.
 */
public class DirectoryScanner {
    private Logger log = LoggerFactory.getLogger(DirectoryScanner.class);

    private boolean recursive;
    private Path dir;
    private List<TemplateDefinition> templateDefinitions;
    private List<Template> templates;
    private DirectoryWatchService directoryWatchService;
    private PersistencyInterface persistency = PersistencyRegistry.getDefault();

    /**
     * @param dir
     * @param recursive If true, the directory will be searched recursively for Merlin templates. Default is false.
     */
    public DirectoryScanner(Path dir, boolean recursive) {
        this(dir, recursive, null);
    }

    /**
     * @param dir
     * @param recursive             If true, the directory will be searched recursively for Merlin templates. Default is false.
     * @param directoryWatchService If given, then this directory will be watched for any modifications.
     */
    public DirectoryScanner(Path dir, boolean recursive, DirectoryWatchService directoryWatchService) {
        this.dir = persistency.getCanonicalPath(dir);
        this.recursive = recursive;
        clear();
        if (directoryWatchService != null) {
            this.directoryWatchService = directoryWatchService;
            directoryWatchService.register(dir, recursive);
        }
    }

    public String getCanonicalPath() {
        return this.dir.toString();
    }

    public void clear() {
        templateDefinitions = new ArrayList<>();
        templates = new ArrayList<>();
    }

    /**
     * Scans the directory for template and template definition files. Updates only items if the file is newer than the
     * last update. To force a reload call clear() first.
     */
    public void process() {
        if (!persistency.exists(dir)) {
            log.error("Directory '" + dir.toAbsolutePath() + "' doesn't exist.");
            return;
        }
        processTemplateDefinitions();
        processTemplates();
    }

    private void processTemplateDefinitions() {
        log.info("Scanning directory '" + dir.toAbsolutePath() + "' for Merlin template definitions (xls and xlsx).");
        Date now = new Date();
        Map<String, Path> templateDefintionFilenames = new HashMap<>();
        List<Path> paths = persistency.listFiles(dir, recursive, "xls", "xlsx");
        for (Path path : paths) {
            if (path.getFileName().toString().startsWith("~$")) {
                log.debug("Ignoring backup file '" + path.toAbsolutePath() + "'. Skipping.");
                continue;
            }
            log.info("Scanning file '" + path.toAbsolutePath() + "'.");
            FileDescriptor fileDescriptor = new FileDescriptor().setDirectory(dir).setRelativePath(path)
                    .setLastUpdate(now);
            TemplateDefinition existingTemplateDefinition = getTemplateDefinition(fileDescriptor);
            if (existingTemplateDefinition != null && !existingTemplateDefinition.getFileDescriptor().isModified(path)) {
                log.debug("Skipping file '" + path.toAbsolutePath() + "'. It's not modified since last scan.");
                continue;
            }
            ExcelWorkbook workbook = ExcelWorkbook.create(path);
            TemplateDefinitionExcelReader templateReader = new TemplateDefinitionExcelReader();
            if (!templateReader.isMerlinTemplateDefinition(workbook)) {
                log.info("Skipping file '" + path.toAbsolutePath() + "', no template definition (OK).");
                continue;
            }
            TemplateDefinition templateDefinition = templateReader.readFromWorkbook(workbook, false);
            if (templateDefinition == null) {
                log.info("Skipping file '" + path.toAbsolutePath() + "', no template definition (OK).");
                continue;
            }
            templateDefinition.setFileDescriptor(fileDescriptor);
            if (!templateReader.isValidTemplateDefinition()) {
                log.info("Skipping '" + path.toAbsolutePath() + "'. It's not a valid Merlin template file.");
                continue;
            }
            String id = templateDefinition.getId();
            if (templateDefintionFilenames.containsKey(id)) {
                log.error("Template with id '" + id + "' already read from file '" + templateDefintionFilenames.get(id).toAbsolutePath()
                        + "'. Ignoring file '" + path.toAbsolutePath() + "'.");
                continue;
            }
            log.info("Valid Merlin template definition found: '" + path.toAbsolutePath() + "'.");
            templateDefinitions.add(templateDefinition);
            templateDefintionFilenames.put(templateDefinition.getId(), path);
        }
    }

    private void processTemplates() {
        log.info("Scanning directory '" + dir.toAbsolutePath() + "' for Merlin templates (docx).");
        Date now = new Date();
        List<Path> paths = persistency.listFiles(dir, recursive, "docx");
        for (Path path : paths) {
            if (path.getFileName().toString().startsWith("~$")) {
                log.debug("Ignoring backup file '" + path.toAbsolutePath() + "'. Skipping.");
                continue;
            }
            log.info("Scanning file '" + path.toAbsolutePath() + "'.");
            FileDescriptor fileDescriptor = new FileDescriptor().setDirectory(dir).setRelativePath(path)
                    .setLastUpdate(now);
            Template existingTemplate = getTemplate(fileDescriptor);
            if (existingTemplate != null && !existingTemplate.getFileDescriptor().isModified(path)) {
                log.debug("Skipping file '" + path.toAbsolutePath() + "'. It's not modified since last scan.");
                continue;
            }
            WordDocument doc = WordDocument.create(path);
            WordTemplateChecker templateChecker = new WordTemplateChecker(doc);
            if (CollectionUtils.isEmpty(templateChecker.getTemplate().getStatistics().getAllUsedVariables())) {
                log.debug("Skipping Word document: '" + path.toAbsolutePath()
                        + "'. It's seemd to be not a Merlin template. No variables and conditionals found.");
                continue;
            }
            templateChecker.getTemplate().setFileDescriptor(fileDescriptor);
            String templateDefinitionId = doc.scanForTemplateDefinitionReference();
            if (templateDefinitionId != null) {
                log.debug("Template definition reference found: " + templateDefinitionId);
                TemplateDefinition templateDefinition = null;
                if (CollectionUtils.isEmpty(templateDefinitions)) {
                    log.warn("No templateDefinitions given, can't look for reference: " + templateDefinitionId);
                } else {
                    String id = templateDefinitionId.trim().toLowerCase();
                    for (TemplateDefinition def : templateDefinitions) {
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
                for (TemplateDefinition templateDefinition : templateDefinitions) {
                    if (templateChecker.getTemplate().getFileDescriptor().matches(templateDefinition.getFileDescriptor())) {
                        templateChecker.getTemplate().assignTemplateDefinition(templateDefinition);
                        log.info("Found matching template definition: " + templateDefinition.getFileDescriptor());
                        break;
                    }
                }
            }
            templates.add(templateChecker.getTemplate());
            if (log.isDebugEnabled()) {
                log.debug("Valid Merlin template found: " + templateChecker.getTemplate());
            }
            log.info("Valid Merlin template found: '" + path.toAbsolutePath() + "'.");
        }
    }

    public List<TemplateDefinition> getTemplateDefinitions() {
        return templateDefinitions;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public Template getTemplate(FileDescriptor descriptor) {
        return getTemplate(descriptor.getCanonicalPathString());
    }

    public Template getTemplate(String canonicalPath) {
        for (Template template : templates) {
            if (canonicalPath.equals(template.getFileDescriptor().getCanonicalPathString())) {
                return template;
            }
        }
        return null;
    }

    public TemplateDefinition getTemplateDefinition(FileDescriptor descriptor) {
        for (TemplateDefinition templateDefinition : templateDefinitions) {
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
        String search = id.trim().toLowerCase();
        for (TemplateDefinition templateDefinition : templateDefinitions) {
            if (search.equals(templateDefinition.getId().trim().toLowerCase())) {
                return templateDefinition;
            }
        }
        return null;
    }

    public Path getDir() {
        return dir;
    }

    public boolean isRecursive() {
        return recursive;
    }
}
