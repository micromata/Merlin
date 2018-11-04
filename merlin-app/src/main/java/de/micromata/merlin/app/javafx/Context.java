package de.reinhard.merlin.app.javafx;

import de.reinhard.merlin.UTF8ResourceBundleControl;
import de.reinhard.merlin.app.ConfigurationHandler;
import de.reinhard.merlin.app.Languages;
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
        // Install4j:
        // Installer -> Screens & Actions -> Installation -> + (Add action)
        // Action: Set a key in the Java preference store
        //         Package name:    de.micromata.merlin
        //         Key:             language
        //         Value:           ${installer:sys.languageId}
        //         Preference root: User specific
        String language = ConfigurationHandler.getInstance().get("language", null);
        if (language != null) {
            locale = Languages.asLocale(language, true);
            log.info("Using the language defined by the installer (user's preferences): code=" + language + ", locale=" + locale);
        } else {
            locale = Locale.getDefault();
            log.info("Using the default language of the server host: " + locale);
        }
        ResourceBundle.Control utf8Control = new UTF8ResourceBundleControl();
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, utf8Control);
    }
}
