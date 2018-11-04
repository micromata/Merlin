package de.micromata.merlin.app.rest;

import de.micromata.merlin.app.json.JsonUtils;
import de.micromata.merlin.app.storage.Storage;
import de.micromata.merlin.word.templating.Template;
import de.micromata.merlin.word.templating.TemplateDefinition;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/templates")
public class TemplatesRest {
    @GET
    @Path("refresh")
    @Produces(MediaType.TEXT_PLAIN)
    /**
     * Reloads all templates on the server.
     * @return "OK"
     */
    public String refresh() {
        Storage.getInstance().refresh();
        return "OK";
    }

    @GET
    @Path("definition-list")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getTemplateDefinitionsList(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        List<TemplateDefinition> templateDefinitionsList = Storage.getInstance().getAllTemplateDefinitions();
        return JsonUtils.toJson(templateDefinitionsList, prettyPrinter);
    }

    @GET
    @Path("definition")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param primaryKey Gets the definition by its primary key.
     * @param id Gets the definition by its id.
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getTemplateDefinition(@QueryParam("primaryKey") String primaryKey,
                                        @QueryParam("id") String id, @QueryParam("prettyPrinter") boolean prettyPrinter) {
        String idOrPrimaryKey = primaryKey != null ? primaryKey : id;
        TemplateDefinition templateDefinition = Storage.getInstance().getTemplateDefinition(idOrPrimaryKey);
        return JsonUtils.toJson(templateDefinition, prettyPrinter);
    }

    @GET
    @Path("template")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param primaryKey
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getTemplate(@QueryParam("primaryKey") String primaryKey, @QueryParam("prettyPrinter") boolean prettyPrinter) {
        Template template = Storage.getInstance().getTemplate(primaryKey);
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
        List<TemplateDefinition> templateDefinitionsList = Storage.getInstance().getAllTemplateDefinitions();
        List<Template> templates = Storage.getInstance().getAllTemplates();
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
