package de.reinhard.merlin.app.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a form field which will be rendered by the client (ReactJS).
 */
public class FormEntry {
    private boolean required;
    private FormEntryType type = FormEntryType.STRING;
    private String path;
    private String label;
    private Number minumumValue, maximumValue;
    private List<FormFieldOption> options;
    private FormEntryType childType;

    public FormEntry() {
    }

    public FormEntry(String path) {
        this.path = path;
    }

    public FormEntry(String path, String label) {
        this(path);
        setLabel(label);
    }

    /**
     * JSON path of the bound json object (e. g. property name).
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FormEntryType getType() {
        return type;
    }

    /**
     * @param type Default is {@link FormEntryType#STRING}.
     * @return this for chaining.
     */
    public FormEntry setType(FormEntryType type) {
        this.type = type;
        return this;
    }

    public String getLabel() {
        return label;
    }

    /**
     * @param label
     * @return this for chaining.
     */
    public FormEntry setLabel(String label) {
        this.label = label;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    /**
     * @param required Default is false.
     * @return this for chaining.
     */
    public FormEntry setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public Number getMinumumValue() {
        return minumumValue;
    }

    public FormEntry setMinumumValue(Number minumumValue) {
        this.minumumValue = minumumValue;
        return this;
    }

    public Number getMaximumValue() {
        return maximumValue;
    }

    public FormEntry setMaximumValue(Number maximumValue) {
        this.maximumValue = maximumValue;
        return this;
    }

    public List<FormFieldOption> getOptions() {
        return options;
    }

    public void setOptions(List<FormFieldOption> options) {
        this.options = options;
    }

    /**
     * @param value
     * @param label
     * @return this for chaining.
     */
    public FormEntry addOption(Object value, String label) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(new FormFieldOption(value, label));
        return this;
    }

    /**
     * If type == {@link FormEntryType#LIST}, then a list of childs is supported. The childs are of
     *
     * @return
     */
    public FormEntryType getChildType() {
        return childType;
    }

    public FormEntry setChildType(FormEntryType childType) {
        if (childType != null && type != FormEntryType.LIST) {
            throw new IllegalArgumentException("Can't set child type for form entry of type '" + type + "'");
        }
        this.childType = childType;
        return this;
    }
}
