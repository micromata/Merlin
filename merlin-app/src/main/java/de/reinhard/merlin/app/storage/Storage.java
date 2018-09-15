package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.word.templating.TemplateDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    private Logger log = LoggerFactory.getLogger(Storage.class);
    private static final Storage instance = new Storage();

    private List<TemplateDefinition> templatesList;

    public static Storage getInstance() {
        return instance;
    }

    private Storage() {

    }

    public void add(TemplateDefinition template) {
        getTemplatesList().add(template);
    }

    public List<TemplateDefinition> getTemplatesList() {
        if (templatesList == null) {
            templatesList = new ArrayList<>();
        }
        return templatesList;
    }

    public TemplateDefinition getTemplate(String id) {
        if (templatesList == null || id == null) {
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
