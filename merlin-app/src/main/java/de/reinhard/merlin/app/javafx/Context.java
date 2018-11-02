package de.reinhard.merlin.app.javafx;

import de.reinhard.merlin.UTF8ResourceBundleControl;
import de.reinhard.merlin.app.ConfigurationHandler;
import de.reinhard.merlin.app.Languages;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Context {
    private static Logger log = LoggerFactory.getLogger(Context.class);
    public static final String BUNDLE_NAME = "MerlinAppMessagesBundle";

    private static Context instance = new Context();

    private ResourceBundle resourceBundle;

    private Locale locale;

    public String getString(final String i18nKey) {
        try {
            final String translation = resourceBundle.getString(i18nKey);
            return translation;
        } catch (final MissingResourceException ex) {
            log.error("Internal: CoreI18n key for " + i18nKey + " not found for locale " + String.valueOf(locale) + ".");
            return "???" + i18nKey + "???";
        }
    }

    public String getMessage(final String messageKey, final Object... params) {
        if (params == null) {
            return getString(messageKey);
        }
        return MessageFormat.format(getString(messageKey), params);
    }

    public String getLabel(final String i18nKey) {
        return getString(i18nKey) + ":";
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * Is the os.name of the JVM set to true?
     */
    public static boolean isMacOS() {
        return System.getProperty("os.name").startsWith("Mac");
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public static Context instance() {
        return instance;
    }

    Context() {
        String lang = ConfigurationHandler.getInstance().getConfiguration().getServerLanguage();
        if (StringUtils.isNotBlank(lang)) {
            locale = Languages.asLocale(lang, true);
        } else {
            locale = Locale.getDefault();
        }
        ResourceBundle.Control utf8Control = new UTF8ResourceBundleControl();
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, utf8Control);
    }
}
