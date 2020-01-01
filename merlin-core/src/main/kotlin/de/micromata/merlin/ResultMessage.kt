package de.micromata.merlin

/**
 * Used for validation messages etc. Supports own internationalization and defining of messages.
 */
open class ResultMessage
@JvmOverloads constructor(
        /**
         * E. g. for translation and customized messages to display.
         */
        val messageId: String? = null,
        var status: ResultMessageStatus,
        vararg val parameters: Any?) {

    /**
     * @param i18n The i18n implementation to use for translation.
     * @return localized message.
     */
    @JvmOverloads
    open fun getMessage(i18n: I18n = CoreI18n.getDefault()): String? {
        return i18n.formatMessage(messageId, *parameters)
    }

    val isOK: Boolean
        get() = status == ResultMessageStatus.OK

    val isWarning: Boolean
        get() = status == ResultMessageStatus.WARNING

    val isError: Boolean
        get() = status == ResultMessageStatus.ERROR
}
