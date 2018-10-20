package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.updater.AppUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/updates")
public class UpdateServiceRest {
    private Logger log = LoggerFactory.getLogger(UpdateServiceRest.class);

    @GET
    @Path("/install")
    @Produces(MediaType.TEXT_PLAIN)
    public String downloadUpdate(@Context HttpServletRequest requestContext) {
        String msg = FilesServiceRest.checkLocalDesktopAvailable(requestContext);
        if (msg != null) {
            log.error("Can't launch updater from remote client (only from localhost): " + msg);
            return msg;
        }
        boolean result = AppUpdater.getInstance().install();
        return result ? "OK" : "ERROR";
    }
}
