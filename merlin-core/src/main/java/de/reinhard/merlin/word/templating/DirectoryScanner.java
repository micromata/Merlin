package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.word.WordDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Searches in directories for Merlin template definition files.
 */
public class DirectoryScanner {
    private Logger log = LoggerFactory.getLogger(DirectoryScanner.class);

    private boolean recursive;
    private File dir;
    private List<TemplateDefinition> templateDefinitions;
    private List<Template> templates;
    private Map<String, File> templateDefintionFilenames;

    public void process(File dir) {
        process(dir, false);
    }

    /**
     * @param dir
     * @param recursive If true, the directory will be searched recursively for Merlin templates. Default is false.
     */
    public void process(File dir, boolean recursive) {
        if (!dir.exists()) {
            log.error("Directory '" + dir.getAbsolutePath() + "' doesn't exist.");
            return;
        }
        this.dir = dir;
        processTemplateDefinitions(dir, recursive);
        processTemplates(dir, recursive);
    }

    /**
     * @param dir
     * @param recursive If true, the directory will be searched recursively for Merlin templates. Default is false.
     */
    private void processTemplateDefinitions(File dir, boolean recursive) {
        log.info("Scanning directory '" + dir.getAbsolutePath() + "' for Merlin template definitions (xls and xlsx).");
        templateDefinitions = new ArrayList<>();
        templateDefintionFilenames = new HashMap<>();
        List<File> files = (List<File>) FileUtils.listFiles(dir, new String[]{"xls", "xlsx"}, recursive);
        for (File file : files) {
            if (file.getName().startsWith("~$")) {
                log.debug("Ignoring backup file '" + file.getAbsolutePath() + "'. Skipping.");
                continue;
            }
            log.info("Scanning file '" + file.getAbsolutePath() + "'.");
            ExcelWorkbook workbook;
            try {
                workbook = new ExcelWorkbook(file);
            } catch (Exception ex) {
                log.info("Can't parse '" + file.getAbsolutePath() + "'. Skipping.");
                continue;
            }
            TemplateDefinitionExcelReader templateReader = new TemplateDefinitionExcelReader();
            TemplateDefinition templateDefinition = templateReader.readFromWorkbook(workbook);
            FileLocation fileLocation = new FileLocation().setDirectory(dir).setRelativePath(file.getParent())
                    .setFilename(file.getName());
            templateDefinition.setFileLocation(fileLocation);
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

    /**
     * @param dir
     * @param recursive If true, the directory will be searched recursively for Merlin templates. Default is false.
     */
    private void processTemplates(File dir, boolean recursive) {
        log.info("Scanning directory '" + dir.getAbsolutePath() + "' for Merlin templates (docx).");
        templates = new ArrayList<>();
        List<File> files = (List<File>) FileUtils.listFiles(dir, new String[]{"docx"}, recursive);
        for (File file : files) {
            if (file.getName().startsWith("~$")) {
                log.debug("Ignoring backup file '" + file.getAbsolutePath() + "'. Skipping.");
                continue;
            }
            log.info("Scanning file '" + file.getAbsolutePath() + "'.");
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
            FileLocation fileLocation = new FileLocation().setDirectory(dir).setRelativePath(file.getParent())
                    .setFilename(file.getName());
            templateChecker.getTemplate().setFileLocation(fileLocation);
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
                    if (templateChecker.getTemplate().getFileLocation().matches(templateDefinition.getFileLocation())) {
                        templateChecker.getTemplate().assignTemplateDefinition(templateDefinition);
                        log.info("Found matching template definition: " + templateDefinition.getFileLocation());
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

    public File getTemplateFile(String templateId) {
        return templateDefintionFilenames.get(templateId);
    }
}
