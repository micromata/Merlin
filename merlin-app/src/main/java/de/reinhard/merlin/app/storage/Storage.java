package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.word.templating.TemplateDefinition;

import java.util.LinkedList;
import java.util.List;

public class Storage {
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
            templatesList = new LinkedList<>();
        }
        return templatesList;
    }

}
