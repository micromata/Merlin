package de.micromata.merlin.importer;

/**
 * Represents a property change.
 */
public class PropertyDelta {
    String property;
    Object oldValue;
    Object newValue;

    public String getProperty() {
        return this.property;
    }

    public Object getOldValue() {
        return this.oldValue;
    }

    public Object getNewValue() {
        return this.newValue;
    }
}
