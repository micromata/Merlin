package de.micromata.merlin;

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
     * @return Message including parameters etc. localized with {@link CoreI18n#getDefault()}.
     */
    public String getMessage() {
        return getMessage(CoreI18n.getDefault());
    }

    public Object[] getParameters() {
        return parameters;
    }

    /**
     * @param i18n
     * @return localized message.
     */
    public String getMessage(I18n i18n) {
        return i18n.formatMessage(messageId, parameters);
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
