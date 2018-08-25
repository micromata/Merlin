package de.reinhard.merlin;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Used for validation messages etc. Supports own internationalization and defining of messages.
 */
public class ResultMessage {
    private String messageId;
    private Object[] parameters;
    private ResultMessageStatus status;

    /**
     * No message, status = OK.
     */
    public ResultMessage() {
        this.status = ResultMessageStatus.OK;
    }

    public ResultMessage(String messageId, ResultMessageStatus status, Object... parameters) {
        this.messageId = messageId;
        this.status = status;
        this.parameters = parameters;
    }

    /**
     * E. g. for translation and customized messages to display.
     *
     * @return Code of the result message.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @return Non-localized message including parameters etc.
     */
    public String getMessage() {
        return getMessage(I18n.getInstance().getResourceBundle());
    }

    /**
     * @param resourceBundle
     * @return localized message.
     */
    public String getMessage(ResourceBundle resourceBundle) {
        return MessageFormat.format(resourceBundle.getString(messageId), getParameters());
    }


    /**
     * @return parameters to display as part of the message.
     */
    public Object[] getParameters() {
        return parameters;
    }

    public boolean isOK() {
        return status == ResultMessageStatus.OK;
    }

    public boolean isWarning() {
        return status == ResultMessageStatus.WARNING;
    }

    public boolean isError() {
        return status == ResultMessageStatus.ERROR;
    }
}
