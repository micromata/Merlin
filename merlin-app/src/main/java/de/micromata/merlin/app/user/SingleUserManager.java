package de.reinhard.merlin.app.user;

import de.reinhard.merlin.app.ConfigurationHandler;
import de.reinhard.merlin.app.Languages;
import de.reinhard.merlin.app.RunningMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Contains only one (dummy) user (for desktop version).
 */
public class SingleUserManager extends UserManager {
    private static final String USER_LOCAL_PREF_KEY = "userLocale";
    private static Logger log = LoggerFactory.getLogger(SingleUserManager.class);
    private UserData singleUser;

    public SingleUserManager() {
        if (RunningMode.getServerType() != RunningMode.ServerType.DESKTOP) {
            throw new IllegalStateException("Can't use SingleUserManager in mode '" + RunningMode.getServerType()
                    + "'. Only allowed in '" + RunningMode.ServerType.DESKTOP + "'.");
        }
        log.info("Using SingleUserManger as user manager.");
        singleUser = new UserData();
        singleUser.setUsername("admin");
        singleUser.setAdmin(true);
        String language = ConfigurationHandler.getInstance().get("userLocale", null);
        Locale locale = Languages.asLocale(language);
        singleUser.setLocale(locale);
    }

    public UserData getUser(String id) {
        return singleUser;
    }

    /**
     * Stores only the user's configured locale.
     *
     * @param userData
     * @see ConfigurationHandler#save(String, String)
     */
    @Override
    public void saveUser(UserData userData) {
        Locale locale = userData.getLocale();
        this.singleUser.setLocale(locale);
        String lang = Languages.asString(locale);
        ConfigurationHandler.getInstance().save("userLocale", lang);
    }
}
