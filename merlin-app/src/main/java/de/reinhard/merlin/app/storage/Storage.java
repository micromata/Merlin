package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.word.templating.DirectoryScanner;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {
    private Logger log = LoggerFactory.getLogger(Storage.class);
    private static final Storage instance = new Storage();

    private Map<String, DirectoryScanner> directoryScannerMap = new HashMap<>();

    public static Storage getInstance() {
        return instance;
    }

    private Storage() {
    }

    public void add(File directory, boolean recursive) {
        DirectoryScanner directoryScanner = new DirectoryScanner(directory, recursive);
        directoryScanner.process();
        directoryScannerMap.put(directory.getAbsolutePath(), directoryScanner);
    }

    public void add(DirectoryScanner directoryScanner) {
        directoryScannerMap.put(directoryScanner.getDir().getAbsolutePath(), directoryScanner);
    }

    public List<TemplateDefinition> getTemplateDefinitions() {
        List<TemplateDefinition> templateDefinitions = new ArrayList<>();
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            if (CollectionUtils.isNotEmpty(directoryScanner.getTemplateDefinitions())) {
                templateDefinitions.addAll(directoryScanner.getTemplateDefinitions());
            }
        }
        return templateDefinitions;
    }

    public TemplateDefinition getTemplateDefinition(String idOrName) {
        if (idOrName == null) {
            return null;
        }
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            TemplateDefinition templateDefinition = directoryScanner.getTemplateDefinition(idOrName);
            if (templateDefinition != null) {
                return templateDefinition;
            }
        }
        log.info("Template definition with id or name '" + idOrName + "' not found.");
        return null;
    }

    public List<Template> getTemplates() {
        List<Template> templates = new ArrayList<>();
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            if (CollectionUtils.isNotEmpty(directoryScanner.getTemplates())) {
                templates.addAll(directoryScanner.getTemplates());
            }
        }
        return templates;
    }

    public Template getTemplate(String canonicalPath) {
        for (DirectoryScanner directoryScanner : directoryScannerMap.values()) {
            if (CollectionUtils.isNotEmpty(directoryScanner.getTemplates())) {
                for (Template template : directoryScanner.getTemplates()) {
                    if(canonicalPath.equals(template.getFileDescriptor().getCanonicalPath())) {
                        return template;
                    }
                }
            }
        }
        return null;
    }
}
