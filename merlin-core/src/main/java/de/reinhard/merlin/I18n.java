package de.reinhard.merlin;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * For internationalization.
 */
public class I18n {
    private static I18n defaultInstance = new I18n();

    public static I18n getDefault() {
        return defaultInstance;
    }

    /**
     * Use this if only one locale is used (in a none multi user system).
     * At default the default message bundle "MessagesBundle" of the class path with the system's default locale is used.
     *
     * @param instance
     */
    public static void setDefault(I18n instance) {
        defaultInstance = instance;
    }

    /**
     * Use this if only one locale is used (in a none multi user system).
     * Uses bundle "MessagesBundle" of the class path with the given locale.
     *
     * @param locale
     * @return new default instance for chaining.
     */
    public static I18n setDefault(Locale locale) {
        defaultInstance = new I18n(locale);
        return defaultInstance;
    }

    private ResourceBundle resourceBundle;

    /**
     * Uses the default message bundle "MessagesBundle" of class path with systems default locale.
     */
    public I18n() {
        ResourceBundle.Control utf8Control = new UTF8ResourceBundleControl();
        this.resourceBundle = ResourceBundle.getBundle("MessagesBundle", utf8Control);
    }

    public I18n(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public I18n(Locale locale) {
        ResourceBundle.Control utf8Control = new UTF8ResourceBundleControl();
        this.resourceBundle = ResourceBundle.getBundle("MessagesBundle", locale, utf8Control);
    }

    /**
     * @param messageId
     * @return localized message.
     */
    public String getMessage(String messageId) {
        return resourceBundle.getString(messageId);
    }

    /**
     * @param messageId
     * @param params    Message parameter to replace in message.
     * @return localized message.
     * @see MessageFormat#format(String, Object...)
     */
    public String formatMessage(String messageId, Object... params) {
        return MessageFormat.format(resourceBundle.getString(messageId), params);
    }
}
