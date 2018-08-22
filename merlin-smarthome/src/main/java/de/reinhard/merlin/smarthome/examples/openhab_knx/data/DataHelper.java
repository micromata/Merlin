package de.reinhard.merlin.smarthome.examples.openhab_knx.data;

import org.apache.commons.lang.StringUtils;

public class DataHelper {
    public static int getMax(int actutalMax, String val) {
        if (val == null) return actutalMax;
        return val.length() > actutalMax ? val.length() : actutalMax;
    }

    public static String rightPad(String val, int maxLength) {
        return StringUtils.rightPad(val, maxLength);
    }
}
