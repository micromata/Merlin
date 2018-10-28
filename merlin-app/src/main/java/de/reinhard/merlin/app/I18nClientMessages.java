package de.reinhard.merlin.app;

import de.reinhard.merlin.CoreI18n;
import de.reinhard.merlin.UTF8ResourceBundleControl;

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

    public Map<String, String> getAllMessages(Locale locale) {
        Map<String, String> map = new HashMap<>();
        addAllMessages(map, new AppI18n(locale).getResourceBundle());
        addAllMessages(map, new CoreI18n(locale).getResourceBundle());
        ResourceBundle.Control utf8Control = new UTF8ResourceBundleControl();
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, utf8Control);
        addAllMessages(map, bundle);
        return new TreeMap<>(map); // Sorted by keys.
    }

    private Map<String, String> addAllMessages(Map<String, String> map, ResourceBundle bundle) {
        for (String key : bundle.keySet()) {
            String value = bundle.getString(key);
            map.put(key, prepareForReactClient(value));
        }
        return map;
    }

    /**
     * @param str
     * @return
     * @see MessageFormat#format(Object)
     */
    static String prepareForReactClient(String str) {
        return MessageFormat.format(str, params);
    }
}
