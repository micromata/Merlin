package de.micromata.merlin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * For internationalization.
 */
public class I18n {
    private Logger log = LoggerFactory.getLogger(I18n.class);
    private ResourceBundle resourceBundle;
    private Map<Locale, I18n> i18nMap;

    public I18n get(Locale locale) {
        if (i18nMap == null) {
            i18nMap = new HashMap<>();
        }
        I18n i18n = i18nMap.get(locale);
        if (i18n == null) {
            i18n = create(locale);
            i18nMap.put(locale, i18n);
        }
        return i18n;
    }

    protected I18n create(Locale locale) {
        return new I18n(this.resourceBundle.getBaseBundleName(), locale);
    }

    /**
     * Uses the default message bundle "MessagesBundle" of class path with systems default locale.
     */
    public I18n(String bundleName) {
        ResourceBundle.Control utf8Control = new UTF8ResourceBundleControl();
        this.resourceBundle = ResourceBundle.getBundle(bundleName, utf8Control);
    }

    public I18n(String bundleName, Locale locale) {
        ResourceBundle.Control utf8Control = new UTF8ResourceBundleControl();
        this.resourceBundle = ResourceBundle.getBundle(bundleName, locale, utf8Control);
    }

    /**
     * Throws an error if messageId not found.
     *
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
        if (params == null || params.length == 0) {
            return getMessage(messageId);
        }
        try {
            return MessageFormat.format(resourceBundle.getString(messageId), params);
        } catch (MissingResourceException ex) {
            log.error("Missing resource '" + messageId + "': " + ex.getMessage(), ex);
            return messageId;
        }
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}
