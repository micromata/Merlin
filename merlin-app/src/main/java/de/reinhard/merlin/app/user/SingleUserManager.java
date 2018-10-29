package de.reinhard.merlin.app.user;

import de.reinhard.merlin.app.RunningMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains only one user (for desktop version).
 */
public class SingleUserManager extends UserManager {
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
    }

    public UserData getUser(String id) {
        return singleUser;
    }
}
