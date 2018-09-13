package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.word.templating.TemplateDefinition;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/templates")
public class TemplateListRest {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTemplatesList() {
        List<TemplateDefinition> templatesList = Storage.getInstance().getTemplatesList();
        return JsonUtils.toJson(templatesList);
    }
}
