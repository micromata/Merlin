package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.word.templating.TemplateDefinition;
import org.apache.commons.lang.StringUtils;
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

    private List<TemplateDefinition> templatesList = new ArrayList<>();

    private Map<String, File> templateFilenames = new HashMap<>();

    public static Storage getInstance() {
        return instance;
    }

    private Storage() {
    }

    public void add(TemplateDefinition template, File file) {
        if (StringUtils.isBlank(template.getId())) {
            log.error("Can't store template without id: '" + template.getName() + "'.");
            return;
        }
        if (getTemplate(template.getId()) != null) {
            log.info("Template with id already exist. Overwriting it: '" + template.getId() + "'.");
        }
        templatesList.add(template);
        templateFilenames.put(template.getId(), file);
    }

    public List<TemplateDefinition> getTemplatesList() {
        return templatesList;
    }

    public TemplateDefinition getTemplate(String id) {
        if (id == null) {
            return null;
        }
        for (TemplateDefinition templateDefinition : templatesList) {
            if (id.equals(templateDefinition.getId())) {
                return templateDefinition;
            }
        }
        log.info("Template with id '" + id + "' not found.");
        return null;
    }
}
