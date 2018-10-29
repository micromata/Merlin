package de.reinhard.merlin.app.user;

/**
 * Handles all user data.
 */
public abstract class UserManager {
    private static UserManager instance;

    public static void setUserManager(UserManager userManager) {
        instance = userManager;
    }

    public static UserManager instance() {
        return instance;
    }

    public abstract UserData getUser(String id);
}
