package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/templates")
public class TemplatesRest {
    @GET
    @Path("definition-list")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getTemplateDefinitionsList(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        List<TemplateDefinition> templateDefinitionsList = Storage.getInstance().getTemplateDefinitions();
        return JsonUtils.toJson(templateDefinitionsList, prettyPrinter);
    }

    @GET
    @Path("definition/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getTemplateDefinition(@PathParam("id") String id, @QueryParam("prettyPrinter") boolean prettyPrinter) {
        TemplateDefinition templateDefinition = Storage.getInstance().getTemplateDefinition(id);
        return JsonUtils.toJson(templateDefinition, prettyPrinter);
    }

    @GET
    @Path("template")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getTemplate(@QueryParam("canonicalPath") String canonicalPath, @QueryParam("prettyPrinter") boolean prettyPrinter) {
        Template template = Storage.getInstance().getTemplate(canonicalPath);
        return JsonUtils.toJson(template, prettyPrinter);
    }

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getList(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        Data data = new Data();
        List<TemplateDefinition> templateDefinitionsList = Storage.getInstance().getTemplateDefinitions();
        List<Template> templates = Storage.getInstance().getTemplates();
        data.setTemplateDefinitions(templateDefinitionsList);
        data.setTemplates(templates);
        return JsonUtils.toJson(data, prettyPrinter);
    }
}
