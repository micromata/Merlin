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
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getTemplatesList(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        List<TemplateDefinition> templatesList = Storage.getInstance().getTemplatesList();
        return JsonUtils.toJson(templatesList, prettyPrinter);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getTemplate(@PathParam("id") String id, @QueryParam("prettyPrinter") boolean prettyPrinter) {
        TemplateDefinition templateDefinition = Storage.getInstance().getTemplate(id);
        return JsonUtils.toJson(templateDefinition, prettyPrinter);
    }
}
