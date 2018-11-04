package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.Configuration;
import de.reinhard.merlin.app.ConfigurationHandler;
import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.user.UserData;
import de.reinhard.merlin.app.user.UserManager;
import org.apache.commons.lang3.StringUtils;
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
        Configuration config = new Configuration();
        config.copyFrom(ConfigurationHandler.getInstance().getConfiguration());
        String json = JsonUtils.toJson(config, prettyPrinter);
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
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getUser(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        UserData user = RestUtils.getUser();
        String json = JsonUtils.toJson(user, prettyPrinter);
        return json;
    }

    @POST
    @Path("user")
    @Produces(MediaType.TEXT_PLAIN)
    public void setUser(String jsonConfig) {
        UserData user = JsonUtils.fromJson(UserData.class, jsonConfig);
        if (user.getLocale() != null && StringUtils.isBlank(user.getLocale().getLanguage())) {
            // Don't set locale with "" as language.
            user.setLocale(null);
        }
        UserManager.instance().saveUser(user);
    }

    /**
     * Resets the settings to default values (deletes all settings).
     */
    @GET
    @Path("reset")
    @Produces(MediaType.APPLICATION_JSON)
    public String resetConfig(@QueryParam("IKnowWhatImDoing") boolean securityQuestion) {
        if (securityQuestion) {
            ConfigurationHandler.getInstance().removeAllSettings();
        }
        return getConfig(false);
    }
}
