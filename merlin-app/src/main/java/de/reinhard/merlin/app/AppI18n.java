package de.reinhard.merlin.app;

import de.reinhard.merlin.I18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * For internationalization.
 */
public class AppI18n extends I18n {
    public static final String BUNDLE_NAME = "MerlinAppMessagesBundle";

    /**
     * Uses the default message bundle "MessagesBundle" of class path with systems default locale.
     */
    public AppI18n() {
        super(BUNDLE_NAME);
    }

    public AppI18n(Locale locale) {
        super(BUNDLE_NAME, locale);
    }

    public Map<String, String> getAllMessages() {
        Map<String, String> map = new HashMap<>();
        ResourceBundle bundle = super.getResourceBundle();
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        return map;
    }
}
