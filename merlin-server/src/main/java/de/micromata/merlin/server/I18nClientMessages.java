package de.micromata.merlin.server;

import de.micromata.merlin.CoreI18n;

import java.text.MessageFormat;
import java.util.*;

/**
 * For internationalization.
 */
public class I18nClientMessages {
    private static final String BUNDLE_NAME = "MerlinClientMessagesBundle";
    private static String[] params = {"{0}", "{1}", "{2}", "{3}", "{4}", "{5}", "{6}", "{7}", "{8}", "{9}"};
    private static final I18nClientMessages instance = new I18nClientMessages();

    public static I18nClientMessages getInstance() {
        return instance;
    }

    private I18nClientMessages() {
    }

    /**
     *
     * @param locale
     * @param keysOnly If true, only the keys will be returned. Default is false.
     * @return
     */
    public Map<String, String> getAllMessages(Locale locale, boolean keysOnly) {
        Map<String, String> map = new HashMap<>();
        //addAllMessages(map, new AppI18n(locale).getResourceBundle(), keysOnly);
        addAllMessages(map, new CoreI18n(locale).getResourceBundle(), keysOnly);
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        addAllMessages(map, bundle, keysOnly);
        return new TreeMap<>(map); // Sorted by keys.
    }

    private Map<String, String> addAllMessages(Map<String, String> map, ResourceBundle bundle, boolean keysOnly) {
        for (String key : bundle.keySet()) {
            String value = bundle.getString(key);
            map.put(key, keysOnly ? "" : prepareForReactClient(value));
        }
        return map;
    }

    /**
     * @param str
     * @return
     * @see MessageFormat#format(Object)
     */
    static String prepareForReactClient(String str) {
        return MessageFormat.format(str, (Object[])params);
    }
}
