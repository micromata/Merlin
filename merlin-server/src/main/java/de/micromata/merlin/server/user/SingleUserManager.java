package de.micromata.merlin.server.user;

import de.micromata.merlin.server.ConfigurationHandler;
import de.micromata.merlin.server.Languages;
import de.micromata.merlin.server.RunningMode;
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
        String dateFormat = ConfigurationHandler.getInstance().get("userDateFormat", null);
        singleUser.setDateFormat(dateFormat);
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
        String dateFormat = userData.getDateFormat();
        this.singleUser.setDateFormat(dateFormat);
        String lang = Languages.asString(locale);
        ConfigurationHandler.getInstance().save("userLocale", lang);
        ConfigurationHandler.getInstance().save("userDateFormat", dateFormat);
    }
}
