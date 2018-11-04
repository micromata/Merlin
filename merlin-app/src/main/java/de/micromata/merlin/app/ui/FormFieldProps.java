package de.reinhard.merlin.app.ui;

/**
 * Represents the props of an input field (ReactJS).
 */
public class FormFieldProps {
    private boolean required;
    private Number min, max;

    public FormFieldProps() {
    }

    public boolean isRequired() {
        return required;
    }

    /**
     * @param required Default is false.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    public Number getMin() {
        return min;
    }

    public void setMin(Number min) {
        this.min = min;
    }

    public Number getMax() {
        return max;
    }

    public void setMax(Number max) {
        this.max = max;
    }
}
