package de.reinhard.merlin.app.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/updates")
public class UpdateServiceRest {
    private Logger log = LoggerFactory.getLogger(UpdateServiceRest.class);

    @GET
    @Path("/install")
    @Produces(MediaType.TEXT_PLAIN)
    public String downloadUpdate() {
        return "Not yet implemented.";
    }
}
