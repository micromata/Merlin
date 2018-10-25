package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.javafx.RunningMode;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RestUtils {
    private static SimpleDateFormat ISO_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * @return null, if the local app (JavaFX) is running and the request is from localhost. Otherwise message, why local
     * service isn't available.
     */
    static String checkLocalDesktopAvailable(HttpServletRequest requestContext) {
        if (RunningMode.isRunning() == false) {
            return "Service unavailable. No desktop app on localhost available.";
        }
        String remoteAddr = requestContext.getRemoteAddr();
        if (remoteAddr == null || !remoteAddr.equals("127.0.0.1")) {
            return "Service not available. Can't call this service remote. Run this service on localhost of the running desktop app.";
        }
        return null;
    }

    static Response get404Response(Logger log, String errorMessage) {
        log.error(errorMessage);
        Response response = Response.status(404).
                entity(errorMessage).
                type("text/plain").
                build();
        return response;
    }

    static String getISODate() {
        synchronized (ISO_DATEFORMAT) {
            return ISO_DATEFORMAT.format(new Date());
        }
    }
}
