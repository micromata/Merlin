package de.micromata.merlin.smarthome.examples.openhab_knx.data;

import de.micromata.merlin.data.Data;
import org.apache.commons.lang3.StringUtils;

public class KnxThing extends Data {
    public KnxThing() {
        super("KnxThing");
    }

    public String getId() {
        return getString("Id");
    }

    public String getDevice() {
        return getString("Device");
    }

    public String getPersistency() {
        return getString("Persistency");
    }

    public String getFixedKnxChannelType() {
        String str = getString("KNX-Channel-Type");
        if (StringUtils.isBlank(str)) {
            str = getString("Type");
            str = StringUtils.lowerCase(str);
        }
        int maxLength = Integer.max(getMaxLength("KNX-Channel-Type"), getMaxLength("Type"));
        return StringUtils.rightPad(str, maxLength);
    }

    public String getFormattedGa() {
        String ga = getString("ga");
        if (ga == null) {
            return "";
        }
        if (ga.indexOf('[') >= 0) {
            return ga;
        }
        return "[" + ga + "]";
    }

    public String getFixedLabelWithFormat() {
        String format = getString("Format");
        String label = getString("Label");
        String str = StringUtils.isNotBlank(format) ? label + " [" + format + "]" : label;
        int maxLengthLabel = getMaxLength("Label");
        int maxLengthFormat = getMaxLength("Format");
        return StringUtils.rightPad("\"" + str + "\"", maxLengthLabel + maxLengthFormat + 5);
    }
}
