package de.reinhard.merlin;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * For internationalization.
 */
public class CoreI18n extends I18n {
    private static Logger log = LoggerFactory.getLogger(CoreI18n.class);
    private static Locale[] DEFAULT_LOCALES = {Locale.ROOT, Locale.GERMAN};

    private static CoreI18n defaultInstance = new CoreI18n();
    public static final String BUNDLE_NAME = "MerlinCoreMessagesBundle";
    private static List<I18n> allResourceBundles = new ArrayList<>();

    public static CoreI18n getDefault() {
        return defaultInstance;
    }

    /**
     * Use this if only one locale is used (in a none multi user system).
     * At default the default message bundle "MessagesBundle" of the class path with the system's default locale is used.
     *
     * @param instance
     */
    public static void setDefault(CoreI18n instance) {
        defaultInstance = instance;
    }

    /**
     * Use this if only one locale is used (in a none multi user system).
     * Uses bundle "MessagesBundle" of the class path with the given locale.
     *
     * @param locale
     * @return new default instance for chaining.
     */
    public static CoreI18n setDefault(Locale locale) {
        defaultInstance = new CoreI18n(locale);
        return defaultInstance;
    }

    public static Set<String> getAllTranslations(String key) {
        Set<String> set = new HashSet<>();
        for (I18n i18n : getAllResourceBundles()) {
            String translation = i18n.getMessage(key);
            if (StringUtils.isNotBlank(translation))
                set.add(translation);
        }
        return set;
    }

    private static List<I18n> getAllResourceBundles() {
        if (!allResourceBundles.isEmpty()) {
            return allResourceBundles;
        }
        synchronized (allResourceBundles) {
            for (Locale locale : DEFAULT_LOCALES) {
                allResourceBundles.add(new CoreI18n(locale));
            }
        }
        return allResourceBundles;
    }

    /**
     * Uses the default message bundle "MessagesBundle" of class path with systems default locale.
     */
    public CoreI18n() {
        super(BUNDLE_NAME);
    }

    public CoreI18n(Locale locale) {
        super(BUNDLE_NAME, locale);
    }

    @Override
    protected I18n create(Locale locale) {
        return new CoreI18n(locale);
    }

}
