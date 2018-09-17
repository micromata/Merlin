package de.reinhard.merlin.app.ui;

/**
 * Represents a option entry of a FormEntry. The label is displayed for the user.
 */
public class FormFieldOption {
    private Object value;
    private String label;

    public FormFieldOption() {
    }

    public FormFieldOption(Object value, String label) {
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
