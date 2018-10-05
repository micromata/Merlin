package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.app.javafx.RunningMode;
import de.reinhard.merlin.word.templating.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage extends AbstractStorage {
    private Logger log = LoggerFactory.getLogger(Storage.class);
    private static final Storage instance = new Storage();

    private Map<String, Map<String, TemplateDefinition>> templateDefinitionsByDirectoryAndId;

    private Map<String, Template> templatesByCanonicalPath;

    private boolean dirty = true;

    public static Storage getInstance() {
        return instance;
    }

    private Storage() {
        clear();
    }

    private void clear() {
        templateDefinitionsByDirectoryAndId = new HashMap<>();
        templatesByCanonicalPath = new HashMap<>();
    }

    public void add(DirectoryScanner directoryScanner) {
        String directory = directoryScanner.getDir().getAbsolutePath();
        for (Template template : directoryScanner.getTemplates()) {
            templatesByCanonicalPath.put(template.getFileDescriptor().getCanonicalPath(), template);
        }
        Map<String, TemplateDefinition> templateDefinitionMap = templateDefinitionsByDirectoryAndId.get(directory);
        if (templateDefinitionMap == null) {
            templateDefinitionMap = new HashMap<>();
            templateDefinitionsByDirectoryAndId.put(directory, templateDefinitionMap);
        }
        for (TemplateDefinition templateDefinition : directoryScanner.getTemplateDefinitions()) {
            templateDefinitionMap.put(normalizeTemplateId(templateDefinition.getId()), templateDefinition);
        }
    }

    public List<TemplateDefinition> getAllTemplateDefinitions() {
        List<TemplateDefinition> templateDefinitions = new ArrayList<>();
        List<Template> templates = new ArrayList<>();
        for (Map<String, TemplateDefinition> templateDefinitionsMap : templateDefinitionsByDirectoryAndId.values()) {
            for (TemplateDefinition templateDefinition : templateDefinitionsMap.values()) {
                templateDefinitions.add(templateDefinition);
            }
        }
        return templateDefinitions;
    }

    @Override
    public List<TemplateDefinition> getTemplateDefinition(FileDescriptor descriptor, String templateDefinitionId) {
        Validate.notNull(templateDefinitionId);
        templateDefinitionId = normalizeTemplateId(templateDefinitionId);
        List<TemplateDefinition> list = new ArrayList<>();
        if (templateDefinitionId == null) {
            return list;
        }
        for (Map.Entry<String, Map<String, TemplateDefinition>> entry : templateDefinitionsByDirectoryAndId.entrySet()) {
            if (descriptor != null && StringUtils.isNotEmpty(descriptor.getDirectory())) {
                String directory = entry.getKey();
                if (!descriptor.getDirectory().equals(directory)) {
                    // Directory doesn't match directory of the FileDescriptor.
                    continue;
                }
            }
            Map<String, TemplateDefinition> templateDefinitionsMap = entry.getValue();
            TemplateDefinition templateDefinition = templateDefinitionsMap.get(templateDefinitionId);
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
        return list;
    }

    @Override
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
    }

    public List<Template> getAllTemplates() {
        List<Template> templates = new ArrayList<>();
        templates.addAll(templatesByCanonicalPath.values());
        return templates;
    }

    public Template getTemplate(String canonicalPath) {
        return templatesByCanonicalPath.get(normalizeCanonicalPath(canonicalPath));
    }

    public synchronized void refresh() {
        log.info("(Re-loading storage.");
        clear();
        dirty = false;
        if (RunningMode.getMode() == RunningMode.Mode.TemplatesTest) {
            // Creating data for testing.
            TestData.create(RunningMode.getBaseDir());
        }
    }

    private String normalizeTemplateId(String templateId) {
        return templateId != null ? templateId.trim().toLowerCase() : null;
    }

    private String normalizeCanonicalPath(String canonicalPath) {
        return canonicalPath != null ? canonicalPath.trim().toLowerCase() : null;
    }

    private synchronized void checkRefresh() {
        if (!dirty) {
            return;
        }
    }
}
