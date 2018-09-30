package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.word.WordDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Searches in directories for Merlin template definition files and template files.
 * TODO: WatchService watcher = FileSystems.getDefault().newWatchService()
 */
public class DirectoryScanner {
    private Logger log = LoggerFactory.getLogger(DirectoryScanner.class);

    private boolean recursive;
    private File dir;
    private List<TemplateDefinition> templateDefinitions;
    private List<Template> templates;

    /**
     * @param dir
     * @param recursive If true, the directory will be searched recursively for Merlin templates. Default is false.
     */
    public DirectoryScanner(File dir, boolean recursive) {
        this.dir = dir;
        this.recursive = recursive;
        clear();
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
        if (!dir.exists()) {
            log.error("Directory '" + dir.getAbsolutePath() + "' doesn't exist.");
            return;
        }
        processTemplateDefinitions();
        processTemplates();
    }

    private void processTemplateDefinitions() {
        log.info("Scanning directory '" + dir.getAbsolutePath() + "' for Merlin template definitions (xls and xlsx).");
        Date now = new Date();
        Map<String, File> templateDefintionFilenames = new HashMap<>();
        List<File> files = (List<File>) FileUtils.listFiles(dir, new String[]{"xls", "xlsx"}, recursive);
        for (File file : files) {
            if (file.getName().startsWith("~$")) {
                log.debug("Ignoring backup file '" + file.getAbsolutePath() + "'. Skipping.");
                continue;
            }
            log.info("Scanning file '" + file.getAbsolutePath() + "'.");
            FileDescriptor fileDescriptor = new FileDescriptor().setDirectory(dir).setRelativePath(file.getParent())
                    .setFilename(file.getName()).setLastUpdate(now);
            TemplateDefinition existingTemplateDefinition = getTemplateDefinition(fileDescriptor);
            if (existingTemplateDefinition != null && !existingTemplateDefinition.getFileDescriptor().isModified(file)) {
                log.debug("Skipping file '" + file.getAbsolutePath() + "'. It's not modified since last scan.");
                continue;
            }
            ExcelWorkbook workbook;
            try {
                workbook = new ExcelWorkbook(file);
            } catch (Exception ex) {
                log.info("Can't parse '" + file.getAbsolutePath() + "'. Skipping.");
                continue;
            }
            TemplateDefinitionExcelReader templateReader = new TemplateDefinitionExcelReader();
            TemplateDefinition templateDefinition = templateReader.readFromWorkbook(workbook);
            templateDefinition.setFileDescriptor(fileDescriptor);
            if (!templateReader.isValidTemplate()) {
                log.info("Skipping '" + file.getAbsolutePath() + "'. It's not a valid Merlin template file.");
                continue;
            }
            String id = templateDefinition.getId();
            if (templateDefintionFilenames.containsKey(id)) {
                log.error("Template with id '" + id + "' already read from file '" + templateDefintionFilenames.get(id).getAbsolutePath()
                        + "'. Ignoring file '" + file.getAbsolutePath() + "'.");
                continue;
            }
            log.info("Valid Merlin template definition found: '" + file.getAbsolutePath() + "'.");
            templateDefinitions.add(templateDefinition);
            templateDefintionFilenames.put(templateDefinition.getId(), file);
        }
    }

    private void processTemplates() {
        log.info("Scanning directory '" + dir.getAbsolutePath() + "' for Merlin templates (docx).");
        Date now = new Date();
        List<File> files = (List<File>) FileUtils.listFiles(dir, new String[]{"docx"}, recursive);
        for (File file : files) {
            if (file.getName().startsWith("~$")) {
                log.debug("Ignoring backup file '" + file.getAbsolutePath() + "'. Skipping.");
                continue;
            }
            log.info("Scanning file '" + file.getAbsolutePath() + "'.");
            FileDescriptor fileDescriptor = new FileDescriptor().setDirectory(dir).setRelativePath(file.getParent())
                    .setFilename(file.getName()).setLastUpdate(now);
            Template existingTemplate = getTemplate(fileDescriptor);
            if (existingTemplate != null && !existingTemplate.getFileDescriptor().isModified(file)) {
                log.debug("Skipping file '" + file.getAbsolutePath() + "'. It's not modified since last scan.");
                continue;
            }
            WordDocument doc;
            try {
                doc = new WordDocument(file);
            } catch (Exception ex) {
                log.info("Can't parse '" + file.getAbsolutePath() + "'. Skipping.");
                continue;
            }
            WordTemplateChecker templateChecker = new WordTemplateChecker(doc);
            if (CollectionUtils.isEmpty(templateChecker.getTemplate().getAllUsedVariables())) {
                log.debug("Skipping Word document: '" + file.getAbsolutePath()
                        + "'. It's seemd to be not a Merlin template. No variables and conditionals found.");
                continue;
            }
            templateChecker.getTemplate().setFileDescriptor(fileDescriptor);
            TemplateDefinitionReference templateDefinitionReference = doc.scanForTemplateDefinitionReference();
            if (templateDefinitionReference != null) {
                log.debug("Template definition reference found: " + templateDefinitionReference);
                TemplateDefinition templateDefinition = null;
                if (CollectionUtils.isEmpty(templateDefinitions)) {
                    log.warn("No templateDefinitions given, can't look for reference: " + templateDefinitionReference);
                } else {
                    String id = templateDefinitionReference.getTemplateDefinitionId();
                    if (id != null) {
                        id = id.trim().toLowerCase();
                    }
                    String name = templateDefinitionReference.getTemplateName();
                    if (name != null) {
                        name = name.trim();
                    }
                    for (TemplateDefinition def : templateDefinitions) {
                        if (id != null) {
                            if (id.equals(def.getId())) {
                                templateDefinition = def;
                                break;
                            }
                        }
                        if (name != null) {
                            if (name.equals(def.getName())) {
                                templateDefinition = def;
                                break;
                            }
                        }
                    }
                }
                if (templateDefinition != null) {
                    templateChecker.getTemplate().assignTemplateDefinition(templateDefinition);
                } else {
                    log.warn("Template definition not found: " + templateDefinitionReference);
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
            log.info("Valid Merlin template found: '" + file.getAbsolutePath() + "'.");
        }
    }

    public List<TemplateDefinition> getTemplateDefinitions() {
        return templateDefinitions;
    }

    public Template getTemplate(FileDescriptor descriptor) {
        for (Template template : templates) {
            if (descriptor.equals(template.getFileDescriptor())) {
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
     * @param idOrName Id or name of the template definition to search for.
     * @return
     */
    public TemplateDefinition getTemplateDefinition(String idOrName) {
        if (idOrName == null) {
            return null;
        }
        String search = idOrName.trim().toLowerCase();
        for (TemplateDefinition templateDefinition : templateDefinitions) {
            if (search.equals(templateDefinition.getId().trim().toLowerCase())) {
                return templateDefinition;
            }
        }
        return null;
    }
}
