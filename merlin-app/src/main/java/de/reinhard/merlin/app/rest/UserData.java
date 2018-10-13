package de.reinhard.merlin.app.rest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Locale;

/**
 * Stores the user data in Thread local for accessing everywhere inside the rest thread.
 * It's only a dummy and simple implementation.
 */
public class UserData {
    private Locale locale;
    private String username;
    private boolean admin;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        ToStringBuilder tos = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tos.append("username", username);
        tos.append("admin", admin);
        tos.append("locale", locale);
        return tos.toString();
    }
}
