package de.reinhard.merlin;

import java.util.ResourceBundle;

public class I18n {
    private static final I18n instance = new I18n();

    public static I18n getInstance() {
        return instance;
    }

    private ResourceBundle resourceBundle;

    public String getMessage(ResultMessage message) {
        return message.getMessage(resourceBundle);
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    private I18n() {
        resourceBundle = ResourceBundle.getBundle("MessagesBundle");
    }
}
