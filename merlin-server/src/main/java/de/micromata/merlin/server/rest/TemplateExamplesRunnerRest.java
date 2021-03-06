package de.micromata.merlin.server.rest;

import de.micromata.merlin.server.json.JsonUtils;
import de.micromata.merlin.server.storage.Storage;
import de.micromata.merlin.word.templating.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/templates")
public class TemplateExamplesRunnerRest {
    private Logger log = LoggerFactory.getLogger(TemplateExamplesRunnerRest.class);

    @GET
    @Path("example-run-data")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getExampleRundata(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        ExampleData exampleData = createExampleData();
        TemplateRunnerData data = new TemplateRunnerData();
        data.setTemplateDefinitionId(exampleData.templateDefinitionId);
        data.setTemplatePrimaryKey(exampleData.templatePrimaryKey);
        data.put("Gender", "female");
        data.put("Employee", "Berta Smith");
        data.put("Date", "2018/01/01");
        data.put("BeginDate", "2018/11/01");
        data.put("WeeklyHours", 40);
        data.put("NumberOfLeaveDays", 30);
        String json = JsonUtils.toJson(data, prettyPrinter);
        return json;
    }

    @GET
    @Path("example-definitions")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Only for development purposes in RestServices.jsx.
     * @return canonicalPath of the first found template with assigned template definition including name and id
     * of the assigned template definition.
     */
    public String getExampleDefinition() {
        ExampleData data = createExampleData();
        String json = JsonUtils.toJson(data, false);
        return json;
    }

    private static ExampleData createExampleData() {
        ExampleData data = new ExampleData();
        boolean found = false;
        for (Template template : Storage.getInstance().getAllTemplates()) {
            if (template.getTemplateDefinition() != null
                    && "Employment contract definition".equals(template.getTemplateDefinition().getId())) {
                // found template with template definition:
                data.templatePrimaryKey = template.getFileDescriptor().getPrimaryKey();
                data.templateDefinitionId = template.getTemplateDefinitionId();
                found = true;
                break;
            }
        }
        if (!found) {
            data.templatePrimaryKey = "Oups, no template with assigned template definiton found (reset settings)!";
            data.templateDefinitionPrimaryKey = "No template definition found (reset settings)!";
            data.templateDefinitionId = "No template definition found (reset settings)!";
        }
        return data;
    }

    public static class ExampleData {
        String templateDefinitionId;
        String templateDefinitionPrimaryKey;
        String templatePrimaryKey;

        public String getTemplateDefinitionId() {
            return templateDefinitionId;
        }

        public String getTemplatePrimaryKey() {
            return templatePrimaryKey;
        }

        public String getTemplateDefinitionPrimaryKey() {
            return templateDefinitionPrimaryKey;
        }

    }
}
