package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.Configuration;
import de.reinhard.merlin.app.ConfigurationHandler;
import de.reinhard.merlin.app.OldConfiguration;
import de.reinhard.merlin.app.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/configuration")
public class ConfigurationRest {
    private Logger log = LoggerFactory.getLogger(ConfigurationRest.class);

    @GET
    @Path("config")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getConfig(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        String json = JsonUtils.toJson(ConfigurationHandler.getInstance().getConfiguration(), prettyPrinter);
        return json;
    }

    @POST
    @Path("config")
    @Produces(MediaType.TEXT_PLAIN)
    public void setConfig(String jsonConfig) {
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
        Configuration config = configurationHandler.getConfiguration();
        Configuration srcConfig = JsonUtils.fromJson(Configuration.class, jsonConfig);
        config.copyFrom(srcConfig);
        configurationHandler.save();
    }

    @GET
    @Path("config-old")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getOldConfig(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        Configuration configuration = ConfigurationHandler.getInstance().getConfiguration();
        return JsonUtils.toJson(new OldConfiguration(configuration), prettyPrinter);
    }

    @POST
    @Path("config-old")
    @Produces(MediaType.TEXT_PLAIN)
    public void setOldConfig(String jsonConfig) {
        OldConfiguration srcConfig = JsonUtils.fromJson(OldConfiguration.class, jsonConfig);
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
        Configuration config = configurationHandler.getConfiguration();
        srcConfig.copyTo(config);
        configurationHandler.save();
    }
}
