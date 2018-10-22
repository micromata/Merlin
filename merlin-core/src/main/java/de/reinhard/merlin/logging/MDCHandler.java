package de.reinhard.merlin.logging;

import org.slf4j.MDC;

public class MDCHandler {
    private static final int LENGTH = MDCKey.values().length;

    private String[] oldValues = new String[LENGTH];
    private Boolean[] usedValues = new Boolean[LENGTH];

    /**
     * Writes the given value to MDC. Any previous existing value will be preserved.
     *
     * @param key
     * @param value
     */
    public void put(MDCKey key, String value) {
        if (key == null) {
            // Used by AbstractHandler (SerialDatasHandler)
            return;
        }
        oldValues[key.ordinal()] = MDC.get(key.mdcKey());
        MDC.put(key.mdcKey(), value);
        usedValues[key.ordinal()] = true;
    }

    /**
     * Restores old values of MDC key or removes the entries, if no old values exist.
     */
    public void restore() {
        for (int i = 0; i < LENGTH; i++) {
            if (!Boolean.TRUE.equals(usedValues[i]))
                continue;
            String oldValue = oldValues[i];
            String key = MDCKey.values()[i].mdcKey();
            if (oldValue != null) {
                MDC.put(key, oldValue);
            } else {
                MDC.remove(key);
            }
        }
    }
}
