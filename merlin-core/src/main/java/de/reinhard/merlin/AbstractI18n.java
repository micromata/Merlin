package de.reinhard.merlin;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * For internationalization.
 */
public class AbstractI18n {
    private ResourceBundle resourceBundle;

    /**
     * Uses the default message bundle "MessagesBundle" of class path with systems default locale.
     */
    public AbstractI18n(String bundleName) {
        ResourceBundle.Control utf8Control = new UTF8ResourceBundleControl();
        this.resourceBundle = ResourceBundle.getBundle(bundleName, utf8Control);
    }

    public AbstractI18n(String bundleName, Locale locale) {
        ResourceBundle.Control utf8Control = new UTF8ResourceBundleControl();
        this.resourceBundle = ResourceBundle.getBundle(bundleName, locale, utf8Control);
    }

    /**
     * Throws an error if messageId not found.
     * @param messageId
     * @return localized message.
     */
    public String getMessage(String messageId) {
        return resourceBundle.getString(messageId);
    }

    /**
     * @param messageId
     * @return true, if the messageId is found in the bundle, otherwise false.
     */
    public boolean containsMessage(String messageId) {
        return resourceBundle.containsKey(messageId);
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
