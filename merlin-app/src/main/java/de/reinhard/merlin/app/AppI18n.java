package de.reinhard.merlin.app;

import de.reinhard.merlin.I18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * For internationalization.
 */
public class AppI18n extends I18n {
    public static final String BUNDLE_NAME = "MerlinAppMessagesBundle";
    private static String[] params = {"{0}", "{1}", "{2}", "{3}", "{4}", "{5}", "{6}", "{7}", "{8}", "{9}"};

    static {
        Locale.setDefault(Locale.ENGLISH);
    }
    /**
     * Uses the default message bundle "MessagesBundle" of class path with systems default locale.
     */
    public AppI18n() {
        super(BUNDLE_NAME);
    }

    public AppI18n(Locale locale) {
        super(BUNDLE_NAME, locale);
    }

    /**
     *
     * @param prepareForReactClient If true, a pre-processing will be done for all values.
     * @return
     * @see #prepareForReactClient(String)
     */
    public Map<String, String> getAllMessages(boolean prepareForReactClient) {
        Map<String, String> map = new HashMap<>();
        ResourceBundle bundle = super.getResourceBundle();
        for (String key : bundle.keySet()) {
            String value = bundle.getString(key);
            map.put(key, prepareForReactClient ? prepareForReactClient(value) : value);
        }
        return map;
    }

    /**
     *
     * @param str
     * @return
     * @see MessageFormat#format(Object)
     */
    static String prepareForReactClient(String str) {
        return MessageFormat.format(str, params);
    }
}
