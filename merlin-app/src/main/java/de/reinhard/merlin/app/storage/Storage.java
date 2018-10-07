package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.app.javafx.RunningMode;
import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.persistency.DirectoryWatchService;
import de.reinhard.merlin.persistency.PersistencyRegistry;
import de.reinhard.merlin.word.templating.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage implements StorageInterface {
    private Logger log = LoggerFactory.getLogger(Storage.class);
    private static final Storage instance = new Storage();
    DirectoryWatchService directoryWatchService;

    // Key is the canonical path of the directory.
    private Map<String, DirectoryScanner> directoryScannerMap;

    private boolean dirty = true;

    public static Storage getInstance() {
        return instance;
    }

    private Storage() {
        directoryScannerMap = new HashMap<>();
        directoryWatchService = new DirectoryWatchService();
        directoryWatchService.start();
    }

    public void add(DirectoryScanner directoryScanner) {
        directoryScannerMap.put(directoryScanner.getCanonicalPath(), directoryScanner);
        directoryWatchService.register(directoryScanner.getDir(), directoryScanner.isRecursive());
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
    public List<TemplateDefinition> getTemplateDefinition(FileDescriptor descriptor, String templateDefinitionId) {
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

    public Template getTemplate(String canonicalPath) {
        Validate.notNull(canonicalPath);
        checkRefresh();
        boolean directoryScannerFound = false;
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            if (!canonicalPath.startsWith(directoryScanner.getCanonicalPath())) {
                // canonicalPath is not part of this directory scanner.
                continue;
            }
            directoryScannerFound = true;
            Template template = directoryScanner.getTemplate(canonicalPath);
            if (template != null) {
                return template;
            }
        }
        if (directoryScannerFound == false) {
            log.info("No directory scanner matching parent directory of canonical path '" + canonicalPath + "' registered. Can't find template.");
        } else {
            log.info("No template with canonical path '" + canonicalPath + "' found.");
        }
        return null;
    }

    public synchronized void refresh() {
        log.info("(Re-)loading storage.");
        dirty = false;
        if (RunningMode.getMode() == RunningMode.Mode.TemplatesTest) {
            // Creating data for testing.
            TestData.create(RunningMode.getBaseDir());
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
}
