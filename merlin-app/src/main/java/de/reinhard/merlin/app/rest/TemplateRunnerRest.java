package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.word.templating.Template;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Date;

@Path("/templates")
public class TemplateRunnerRest {
    private Logger log = LoggerFactory.getLogger(FilesServiceRest.class);

    @POST
    @Path("run")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response runTemplate(String json) {
        TemplateRunnerData data = JsonUtils.fromJson(TemplateRunnerData.class, json);
        log.info("Running template: definition=" + data.getTemplateDefinitionId() + ", template=" + data.getTemplateCanonicalPath());
        String fileLocation = "Test.xlsx";
        Response response = null;

        File file = new File(fileLocation);
        log.info(file.getAbsolutePath());
        if (file.exists()) {
            Response.ResponseBuilder builder = Response.ok(file);
            builder.header("Content-Disposition", "attachment; filename=" + file.getName());
            response = builder.build();
            log.info("Downloading file '" + file + "', length: " + file.length());
        } else {
            log.error("File not found: " + file.getAbsolutePath());
            response = Response.status(404).
                    entity("FILE NOT FOUND: " + file.getName()).
                    type("text/plain").
                    build();
        }

        return response;
    }

    @GET
    @Path("example")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * @param prettyPrinter If true then the json output will be in pretty format.
     * @see JsonUtils#toJson(Object, boolean)
     */
    public String getConfig(@QueryParam("templateCanonicalPath") String templateCanonicalPath,
                            @QueryParam("prettyPrinter") String templateDefinitionId,
                            @QueryParam("prettyPrinter") boolean prettyPrinter) {
        TemplateRunnerData data = new TemplateRunnerData();
        data.setTemplateDefinitionId(templateDefinitionId);
        data.setTemplateCanonicalPath(templateCanonicalPath);
        data.put("Gender", "female");
        data.put("Employee", "Berta Smith");
        data.put("Date", new Date());
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
        ExampleData data = new ExampleData();
        boolean found = false;
        for (Template template : Storage.getInstance().getTemplates()) {
            if (template.getTemplateDefinition() != null
                    && StringUtils.isNotBlank(template.getTemplateDefinition().getName())) {
                // found template with template definition:
                data.templateCanonicalPath = template.getFileDescriptor().getCanonicalPath();
                data.templateDefinitionId = template.getTemplateDefinitionId();
                data.templateDefinitionName = template.getTemplateDefinition().getName();
                found = true;
                break;
            }
        }
        if (!found) {
            data.templateCanonicalPath = "Oups, no template with assigned template definiton found (reset settings)!";
            data.templateDefinitionName = "No template definition found (reset settings)!";
            data.templateDefinitionId = "No template definition found (reset settings)!";
        }
        String json = JsonUtils.toJson(data, false);
        return json;
    }

    public class ExampleData {
        String templateDefinitionId;
        String templateDefinitionName;
        String templateCanonicalPath;

        public String getTemplateDefinitionId() {
            return templateDefinitionId;
        }

        public String getTemplateCanonicalPath() {
            return templateCanonicalPath;
        }

        public String getTemplateDefinitionName() {
            return templateDefinitionName;
        }
    }
}
