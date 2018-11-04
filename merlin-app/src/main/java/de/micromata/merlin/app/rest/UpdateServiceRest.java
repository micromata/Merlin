package de.micromata.merlin.app.rest;

import de.micromata.merlin.app.json.JsonUtils;
import de.micromata.merlin.app.updater.AppUpdater;
import de.micromata.merlin.app.updater.UpdateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/updates")
public class UpdateServiceRest {
    private Logger log = LoggerFactory.getLogger(UpdateServiceRest.class);

    @GET
    @Path("/install")
    @Produces(MediaType.TEXT_PLAIN)
    public String downloadUpdate(@Context HttpServletRequest requestContext) {
        String msg = RestUtils.checkLocalDesktopAvailable(requestContext);
        if (msg != null) {
            log.error("Can't launch updater from remote client (only from localhost): " + msg);
            return msg;
        }
        boolean result = AppUpdater.getInstance().install();
        return result ? "OK" : "ERROR";
    }

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public String info(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        return JsonUtils.toJson(UpdateInfo.getInstance(), prettyPrinter);
    }
}
