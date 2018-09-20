package de.reinhard.merlin.app.ui.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.ui.Form;
import de.reinhard.merlin.app.ui.FormContainer;
import de.reinhard.merlin.app.ui.FormLabelField;
import de.reinhard.merlin.app.ui.FormLabelFieldValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/configuration")
public class ConfigurationUIRest {
    private Logger log = LoggerFactory.getLogger(ConfigurationUIRest.class);

    @GET
    @Path("config-ui")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getConfig(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        Form form = new Form();
        form.add(new FormLabelField("port", "Port").setValueType(FormLabelFieldValueType.INTEGER).setMinumumValue(0).setMaximumValue(65535));
        form.add(new FormLabelField("language", "Language").addOption("en", "English").addOption("de", "Deutsch"));
        form.add(new FormContainer().setTitle("Template directories").setMultiple(true)
                .addChild(new FormLabelField("templateDir", "Template dir").setValueType(FormLabelFieldValueType.DIRECTORY))
                .addChild(new FormLabelField("recursive", "Recursive").setValueType(FormLabelFieldValueType.CHECKED)));
        return JsonUtils.toJson(form, prettyPrinter);
    }
}
