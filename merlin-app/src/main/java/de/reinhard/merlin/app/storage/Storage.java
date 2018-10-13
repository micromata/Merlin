package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.app.ConfigurationHandler;
import de.reinhard.merlin.app.ConfigurationListener;
import de.reinhard.merlin.app.ConfigurationTemplatesDir;
import de.reinhard.merlin.app.javafx.RunningMode;
import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.persistency.FileDescriptor;
import de.reinhard.merlin.persistency.PersistencyRegistry;
import de.reinhard.merlin.persistency.StorageInterface;
import de.reinhard.merlin.persistency.templates.DirectoryScanner;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import de.reinhard.merlin.word.templating.TemplateDefinitionExcelReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage implements StorageInterface, ConfigurationListener {
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

    public void clear() {
        dirty = true;
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

    @Override
    public List<TemplateDefinition> getTemplateDefinition(de.reinhard.merlin.persistency.FileDescriptor descriptor, String templateDefinitionId) {
        Validate.notNull(templateDefinitionId);
        checkRefresh();
        templateDefinitionId = normalizeTemplateId(templateDefinitionId);
        List<TemplateDefinition> list = new ArrayList<>();
        if (templateDefinitionId == null) {
            return list;
        }
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            if (descriptor != null && StringUtils.isNotEmpty(descriptor.getDirectory())) {
                String directory = directoryScanner.getCanonicalPath();
                if (!descriptor.getDirectory().equals(directory)) {
                    // Directory doesn't match directory of the FileDescriptor.
                    continue;
                }
            }
            TemplateDefinition templateDefinition = directoryScanner.getTemplateDefinition(templateDefinitionId);
            if (templateDefinition == null) {
                continue;
            }
            if (descriptor == null || descriptor.getRelativePath() == null) {
                if (templateDefinitionId.equals(normalizeTemplateId(templateDefinition.getId()))) {
                    list.add(templateDefinition);
                }
            } else {
                if (templateDefinition.getFileDescriptor() == null) {
                    log.error("FileDescriptor of TemplateDefinition is null: " + templateDefinitionId);
                } else if (descriptor.getRelativePath().equals(templateDefinition.getFileDescriptor().getRelativePath())) {
                    if (templateDefinitionId.equals(normalizeTemplateId(templateDefinition.getId()))) {
                        list.add(templateDefinition);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            log.info("Template definition with id '" + templateDefinitionId + "' not found for file descriptor: " + descriptor + ".");
        }
        reloadIfModified(list);
        return list;
    }

    private void reloadIfModified(List<TemplateDefinition> templateDefinitions) {
        checkRefresh();
        if (CollectionUtils.isEmpty(templateDefinitions)) {
            // Nothing to do;
            return;
        }
        for (TemplateDefinition templateDefinition : templateDefinitions) {
            reloadIfModified(templateDefinition);
        }
    }

    private void reloadIfModified(TemplateDefinition templateDefinition) {
        checkRefresh();
        FileDescriptor descriptor = templateDefinition.getFileDescriptor();
        if (descriptor == null) {
            log.warn("No file descriptor given, can't check modification of template definition: '" + templateDefinition.getId() + "'.");
            return;
        }
        Path path = descriptor.getCanonicalPath();
        if (!PersistencyRegistry.getDefault().exists(path)) {
            log.warn("File '" + path.toAbsolutePath() + "' doesn't exist, can't check modification of template definition: '" + templateDefinition.getId() + "'.");
            return;
        }
        if (descriptor.isModified(path)) {
            log.info("Template definition file '" + templateDefinition.getId() + "' modified. Reload from file: " + path.toAbsolutePath());
            TemplateDefinitionExcelReader reader = new TemplateDefinitionExcelReader();
            ExcelWorkbook workbook = ExcelWorkbook.create(path);
            templateDefinition = reader.readFromWorkbook(workbook);
            //templateDefinitionsByDirectoryAndId.put()
        }
    }

   /* @Override
    public void putTemplateDefinition(FileDescriptor fileDescriptor, TemplateDefinition templateDefinition) {
        String directory = "/";
        if (fileDescriptor != null && StringUtils.isNotEmpty(fileDescriptor.getDirectory())) {
            directory = fileDescriptor.getDirectory();
        }
        Map<String, TemplateDefinition> templateDefinitionsMap = templateDefinitionsByDirectoryAndId.get(directory);
        if (templateDefinitionsMap == null) {
            templateDefinitionsMap = new HashMap<>();
            templateDefinitionsByDirectoryAndId.put(directory, templateDefinitionsMap);
        }
        templateDefinitionsMap.put(normalizeTemplateId(templateDefinition.getId()), templateDefinition);
    }

    @Override
    public void putTemplate(String canonicalPath, Template template) {
        templatesByCanonicalPath.put(normalizeCanonicalPath(canonicalPath), template);
    }*/

    public List<Template> getAllTemplates() {
        checkRefresh();
        List<Template> templates = new ArrayList<>();
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            templates.addAll(directoryScanner.getTemplates());
        }
        return templates;
    }

    public Template getTemplate(String hashId) {
        Validate.notNull(hashId);
        checkRefresh();
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            Template template = directoryScanner.getTemplate(hashId);
            if (template != null) {
                return template;
            }
        }
        log.info("No template with hashId '" + hashId + "' found.");
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
        if (RunningMode.getMode() == RunningMode.Mode.TemplatesTest) {
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
