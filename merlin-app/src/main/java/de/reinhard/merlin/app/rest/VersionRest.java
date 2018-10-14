package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.Version;
import de.reinhard.merlin.app.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class VersionRest {
    private Logger log = LoggerFactory.getLogger(VersionRest.class);

    @GET
    @Path("version")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getVersion(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        String json = JsonUtils.toJson(Version.getInstance(), prettyPrinter);
        return json;
    }
}
