package de.reinhard.merlin.app.rest;

public class UserUtils {
    private static final ThreadLocal<UserData> threadUser = new ThreadLocal<UserData>();

    public static UserData getUser() {
        return threadUser.get();
    }

    static void setUser(UserData user) {
        threadUser.set(user);
    }

    public static void removeUser() {
        threadUser.remove();
    }
}
