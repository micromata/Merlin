package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelWorkbook;
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
    private List<TemplateDefinition> templates;
    private Map<String, File> templateFilenames;

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
        log.info("Scanning directory '" + dir.getAbsolutePath() + "' for Merlin templates.");
        templates = new ArrayList<>();
        templateFilenames = new HashMap<>();
        List<File> files = (List<File>) FileUtils.listFiles(dir, new String[]{"xls", "xlsx"}, recursive);
        for (File file : files) {
            log.info("Scanning file '" + file.getAbsolutePath() + "'.");
            ExcelWorkbook workbook = new ExcelWorkbook(file);
            TemplateDefinitionExcelReader templateReader = new TemplateDefinitionExcelReader();
            TemplateDefinition templateDefinition = templateReader.readFromWorkbook(workbook);
            if (!templateReader.isValidTemplate()) {
                log.info("Skipping '" + file.getAbsolutePath() + "'. It's not a valid Merlin template file.");
                continue;
            }
            String id = templateDefinition.getId();
            if (templateFilenames.containsKey(id)) {
                log.error("Template with id '" + id + "' already read from file '" + templateFilenames.get(id).getAbsolutePath()
                        + "'. Ignoring file '" + file.getAbsolutePath() + "'.");
                continue;
            }
            log.info("Valid Merlin template found: '" + file.getAbsolutePath() + "'.");
            templates.add(templateDefinition);
            templateFilenames.put(templateDefinition.getId(), file);
        }
    }
}
