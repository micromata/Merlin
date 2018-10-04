package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
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
        List<Template> resultTemplates = new ArrayList<>();
        for (Template template : templates) {
            resultTemplates.add(ensureTemplateDefinition(template));
        }
        data.setTemplateDefinitions(templateDefinitionsList);
        data.setTemplates(resultTemplates);
        return JsonUtils.toJson(data, prettyPrinter);
    }

    /**
     * Method ensures a template definition without modifiing the original template object.
     *
     * @param template
     * @return If the given template has a template definition, the template itself is returned. If not a clone of the tmeplate
     * will be returned including an auto generated template definition.
     * @see Template#createAutoTemplateDefinition()
     */
    private Template ensureTemplateDefinition(Template template) {
        Template resultTemplate = template;
        if (template.getTemplateDefinition() == null) {
            TemplateDefinition templateDefinition = template.createAutoTemplateDefinition();
            resultTemplate = (Template)template.clone();
            resultTemplate.setTemplateDefinition(templateDefinition);
        }
        return resultTemplate;
    }
}
