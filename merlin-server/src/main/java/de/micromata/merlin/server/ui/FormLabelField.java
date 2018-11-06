package de.micromata.merlin.server.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a form field which will be rendered by the client (ReactJS).
 */
public class FormLabelField extends FormEntry {
    private FormLabelFieldValueType valueType = FormLabelFieldValueType.STRING;
    private String path;
    private String label;
    private List<FormLabelFieldOption> options;
    private String tooltip;
    private FormFieldProps props = new FormFieldProps();

    public FormLabelField() {
    }

    public FormLabelField(String path) {
        this.path = path;
    }

    public FormLabelField(String path, String label) {
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

    public FormLabelFieldValueType getValueType() {
        return valueType;
    }

    /**
     * @param valueType Default is {@link FormLabelFieldValueType#STRING}.
     * @return this for chaining.
     */
    public FormLabelField setValueType(FormLabelFieldValueType valueType) {
        this.valueType = valueType;
        return this;
    }

    public String getLabel() {
        return label;
    }

    /**
     * @param label
     * @return this for chaining.
     */
    public FormLabelField setLabel(String label) {
        this.label = label;
        return this;
    }

    public List<FormLabelFieldOption> getOptions() {
        return options;
    }

    public void setOptions(List<FormLabelFieldOption> options) {
        this.options = options;
    }

    /**
     * @param value
     * @param label
     * @return this for chaining.
     */
    public FormLabelField addOption(Object value, String label) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(new FormLabelFieldOption(value, label));
        return this;
    }

    public String getTooltip() {
        return tooltip;
    }

    /**
     * @param tooltip Tooltip to display as info for the user in the frontend.
     */
    public FormLabelField setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public FormFieldProps getProps() {
        return props;
    }

    public FormLabelField setMinumumValue(Number minumumValue) {
        this.props.setMin(minumumValue);
        return this;
    }

    public FormLabelField setMaximumValue(Number maximumValue) {
        this.props.setMax(maximumValue);
        return this;
    }

    public FormLabelField setRequired(boolean required) {
        this.props.setRequired(required);
        return this;
    }
}
