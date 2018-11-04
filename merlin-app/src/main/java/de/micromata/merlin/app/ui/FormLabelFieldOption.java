package de.micromata.merlin.app.ui;

/**
 * Represents a option entry of a FormEntry. The label is displayed for the user.
 */
public class FormLabelFieldOption {
    private Object value;
    private String label;

    public FormLabelFieldOption() {
    }

    public FormLabelFieldOption(Object value, String label) {
        this.value = value;
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
