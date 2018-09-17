package de.reinhard.merlin.app.ui.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.ui.Form;
import de.reinhard.merlin.app.ui.FormContainer;
import de.reinhard.merlin.app.ui.FormLabelField;
import de.reinhard.merlin.app.ui.FormLabelFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/configuration")
public class ConfigurationUIRest {
    private Logger log = LoggerFactory.getLogger(ConfigurationUIRest.class);

    @GET
    @Path("config-ui")
    @Produces(MediaType.APPLICATION_JSON)
    public String getConfig() {
        Form form = new Form();
        form.add(new FormLabelField("port", "Port").setType(FormLabelFieldType.INTEGER).setMinumumValue(0).setMaximumValue(65535));
        form.add(new FormLabelField("language", "Language").addOption("en", "English").addOption("de", "Deutsch"));
        form.add(new FormContainer().setTitle("Template directories").setMultiple(true)
                .addChild(new FormLabelField("templateDir", "Template dir").setType(FormLabelFieldType.DIRECTORY))
                .addChild(new FormLabelField("recursive", "Recursive").setType(FormLabelFieldType.DIRECTORY)));
        return JsonUtils.toJson(form);
    }
}
