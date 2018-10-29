package de.reinhard.merlin.app.user;

import java.util.Locale;

public class UserUtils {
    private static final ThreadLocal<UserInfo> threadUserInfo = new ThreadLocal<UserInfo>();

    public static UserData getUser() {
        UserInfo user = threadUserInfo.get();
        if (user == null) {
            return null;
        }
        return user.userData;
    }

    public static Locale getUserLocale() {
        UserInfo userInfo = threadUserInfo.get();
        if (userInfo == null) {
            return null;
        }
        UserData user = userInfo.userData;
        Locale locale = user.getLocale();
        if (locale == null) {
            locale = userInfo.requestLocale;
        }
        return locale;
    }

    static void setUser(UserData user, Locale requestLocale) {
        threadUserInfo.set(new UserInfo(user, requestLocale));
    }

    static void removeUser() {
        threadUserInfo.remove();
    }

    private static class UserInfo {
        private UserData userData;
        private Locale requestLocale;

        private UserInfo(UserData userData, Locale requestLocale) {
            this.userData = userData;
            this.requestLocale = requestLocale;
        }
    }
}
