package de.micromata.merlin.smarthome.examples.openhab_knx.data;

import org.apache.commons.lang3.StringUtils;

public class ZoneminderThing {
    private String id, number, label;

    public String getId() {
        return id;
    }

    public String getFixedId() {
        return StringUtils.rightPad(id, 30);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public String getFixedNumber() {
        return StringUtils.rightPad(number, 3);
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLabel() {
        return label;
    }

    public String getFixedLabel() {
        return StringUtils.rightPad("\"" + label + "\"", 40);
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
