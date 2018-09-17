package de.reinhard.merlin.app.ui.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.ui.Form;
import de.reinhard.merlin.app.ui.FormEntry;
import de.reinhard.merlin.app.ui.FormEntryType;
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
        form.add(new FormEntry("port", "Port").setType(FormEntryType.INTEGER).setMinumumValue(0).setMaximumValue(65535));
        form.add(new FormEntry("language", "Language").addOption("en", "English").addOption("de", "Deutsch"));
        form.add(new FormEntry("templateDirs", "Template directories").setType(FormEntryType.LIST)
                .setChildType(FormEntryType.DIRECTORY));
        return JsonUtils.toJson(form);
    }
}
