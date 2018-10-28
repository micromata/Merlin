package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.AppI18n;
import de.reinhard.merlin.app.json.JsonUtils;
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
import java.util.Locale;

@Path("/i18n")
public class I18nRest {
    private Logger log = LoggerFactory.getLogger(I18nRest.class);

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @param locale If not given, the client's language (browser) will be used.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getList(@Context HttpServletRequest requestContext, @QueryParam("prettyPrinter") boolean prettyPrinter,
                          @QueryParam("locale") String locale) {
        Locale localeObject;
        if (StringUtils.isNotBlank(locale)) {
            localeObject = new Locale(locale);
        } else {
            localeObject = requestContext.getLocale();
        }
        AppI18n i18n = new AppI18n(localeObject);
        String json = JsonUtils.toJson(i18n.getAllMessages(true), prettyPrinter);
        return json;
    }
}
