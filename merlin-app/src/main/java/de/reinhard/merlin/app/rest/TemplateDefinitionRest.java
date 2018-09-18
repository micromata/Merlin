package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.word.templating.TemplateDefinition;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/templates")
public class TemplateDefinitionRest {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param stringify If true then the json output will be stringified (in pretty format).
     */
    public String getTemplatesList(@QueryParam("stringify") boolean stringify) {
        List<TemplateDefinition> templatesList = Storage.getInstance().getTemplatesList();
        return JsonUtils.toJson(templatesList, stringify);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTodo(@PathParam("id") String id) {
        TemplateDefinition templateDefinition = Storage.getInstance().getTemplate(id);
        return JsonUtils.toJson(templateDefinition);
    }
}
