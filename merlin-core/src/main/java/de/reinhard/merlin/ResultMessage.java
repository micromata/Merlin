package de.reinhard.merlin;

import java.util.List;

/**
 * Used for validation messages etc. Supports own internationalization and defining of messages.
 */
public class ResultMessage {
    private String messageId;
    private Object[] parameters;
    private String message;
    private ResultMessageStatus status;

    /**
     * No message, status = OK.
     */
    public ResultMessage() {
        this.status = ResultMessageStatus.OK;
    }

    public ResultMessage(String messageId, ResultMessageStatus status, String message, Object... parameters) {
        this.messageId = messageId;
        this.status = status;
        this.message = message;
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
        return message;
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
