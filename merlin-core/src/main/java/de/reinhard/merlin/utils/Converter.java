package de.reinhard.merlin.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Converter {
    private static Logger log = LoggerFactory.getLogger(Converter.class);

    public static Double createDouble(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof String) {
            try {
                return Double.valueOf((String) val);
            } catch (NumberFormatException ex) {
                log.info("Can't parse string as double value: " + val);
            }
        }
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        log.info("Can't convert to double value: " + val + ". It's of type: " + val.getClass());
        return null;
    }

    public static Double createDouble(String val) {
        if (val == null) {
            return null;
        }
        try {
            return Double.valueOf((String) val);
        } catch (NumberFormatException ex) {
            log.info("Can't parse string as double value: " + val);
        }
        return null;
    }

    public static String formatNumber(int number, int maxValue) {
        int numberOfDigits;
        if (maxValue < 10) numberOfDigits = 1;
        else if (maxValue < 100) numberOfDigits = 2;
        else if (maxValue < 1000) numberOfDigits = 3;
        else if (maxValue < 10000) numberOfDigits = 4;
        else numberOfDigits = 5;
        return StringUtils.leftPad(String.valueOf(number), numberOfDigits, '0');
    }
}
