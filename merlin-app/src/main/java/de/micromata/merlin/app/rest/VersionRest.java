package de.micromata.merlin.app.rest;

import de.micromata.merlin.app.Languages;
import de.micromata.merlin.app.Version;
import de.micromata.merlin.app.json.JsonUtils;
import de.micromata.merlin.app.user.UserData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.Locale;

@Path("/")
public class VersionRest {
    private Logger log = LoggerFactory.getLogger(VersionRest.class);

    @GET
    @Path("version")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param requestContext For detecting the user's client locale.
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getVersion(@Context HttpServletRequest requestContext, @QueryParam("prettyPrinter") boolean prettyPrinter) {
        UserData user = RestUtils.getUser();
        String language = Languages.asString(user.getLocale());
        if (StringUtils.isBlank(language)) {
            Locale locale = requestContext.getLocale();
            language = locale.getLanguage();
        }
        MyVersion version = new MyVersion(language);
        String json = JsonUtils.toJson(version, prettyPrinter);
        return json;
    }

    public class MyVersion {
        private Version version;
        private String language;

        private MyVersion(String language) {
            this.version = Version.getInstance();
            this.language = language;
        }

        public String getAppName() {
            return version.getAppName();
        }

        public String getVersion() {
            return version.getVersion();
        }

        public String getBuildDateUTC() {
            return version.getBuildDateUTC();
        }

        public Date getBuildDate() {
            return version.getBuildDate();
        }

        /**
         * @return Version of the available update, if exist. Otherwise null.
         */
        public String getUpdateVersion() {
            return version.getUpdateVersion();
        }

        public String getLanguage() {
            return language;
        }
    }
}
