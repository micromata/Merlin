package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.RunningMode;
import de.reinhard.merlin.app.user.UserData;
import de.reinhard.merlin.app.user.UserUtils;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Locale;

public class RestUtils {
    /**
     * @return null, if the local app (JavaFX) is running and the request is from localhost. Otherwise message, why local
     * service isn't available.
     */
    static String checkLocalDesktopAvailable(HttpServletRequest requestContext) {
        if (RunningMode.getServerType() != RunningMode.ServerType.DESKTOP) {
            return "Service unavailable. No desktop app on localhost available.";
        }
        String remoteAddr = requestContext.getRemoteAddr();
        if (remoteAddr == null || !remoteAddr.equals("127.0.0.1")) {
            return "Service not available. Can't call this service remote. Run this service on localhost of the running desktop app.";
        }
        return null;
    }

    /**
     * @return Returns the user put by the UserFilter.
     * @see UserUtils#getUser()
     * @see de.reinhard.merlin.app.user.UserFilter
     */
    static UserData getUser() {
        UserData user = UserUtils.getUser();
        if (user == null) {
            throw new IllegalStateException("No user given in rest call.");
        }
        return UserUtils.getUser();
    }

    static Locale getUserLocale(HttpServletRequest requestContext) {
        UserData user = RestUtils.getUser();
        Locale locale = user.getLocale();
        if (locale == null) {
            locale = requestContext.getLocale();
        }
        return locale;
    }

    static Response get404Response(Logger log, String errorMessage) {
        log.error(errorMessage);
        Response response = Response.status(404).
                entity(errorMessage).
                type("text/plain").
                build();
        return response;
    }
}
