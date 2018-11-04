package de.micromata.merlin.app.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a form  which will be rendered by the client (ReactJS).
 */
public class Form {
    private List<FormEntry> entries;

    public Form() {
        entries = new ArrayList<>();
    }
    public Form add(FormEntry field) {
        entries.add(field);
        return this;
    }

    public List<FormEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<FormEntry> entries) {
        this.entries = entries;
    }
}
