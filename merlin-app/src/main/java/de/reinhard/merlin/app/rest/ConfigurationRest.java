package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.Configuration;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/configuration")
public class ConfigurationRest {
    @GET
    @Path("port")
    @Produces(MediaType.TEXT_PLAIN)
    public int getPort() {
        return Configuration.getInstance().getPort();
    }

    @POST
    @Path("port")
    @Produces(MediaType.TEXT_PLAIN)
    public void setPort(int port) {
        Configuration.getInstance().setPort(port);
    }
}
