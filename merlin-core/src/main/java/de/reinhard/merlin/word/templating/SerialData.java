package de.reinhard.merlin.word.templating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SerialData {
    private static Logger log = LoggerFactory.getLogger(SerialData.class);

    private List<SerialDataEntry> entries = new ArrayList<>();
    private TemplateDefinition templateDefinition;

    public SerialData() {
    }

    public void add(SerialDataEntry data) {
        entries.add(data);
    }

    public TemplateDefinition getTemplateDefinition() {
        return templateDefinition;
    }

    public void setTemplateDefinition(TemplateDefinition templateDefinition) {
        this.templateDefinition = templateDefinition;
    }

    public List<SerialDataEntry> getEntries() {
        return entries;
    }
}
