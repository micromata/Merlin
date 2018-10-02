package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.word.WordDocument;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import de.reinhard.merlin.word.templating.WordTemplateRunner;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

@Path("/templates")
public class TemplateRunnerRest {
    private Logger log = LoggerFactory.getLogger(FilesServiceRest.class);

    @POST
    @Path("check")
    @Produces(MediaType.APPLICATION_JSON)
    public String checkTemplate(String json) {
        TemplateRunnerData data = JsonUtils.fromJson(TemplateRunnerData.class, json);
        log.info("Checking template: definition=" + data.getTemplateDefinitionId() + ", template=" + data.getTemplateCanonicalPath());
        TemplateRunnerCheckData check = new TemplateRunnerCheckData();
        check.setStatus("Not yet implemented");
        String result = JsonUtils.toJson(check);
        return result;
    }

    @POST
    @Path("run")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response runTemplate(String json) {
        TemplateRunnerData data = JsonUtils.fromJson(TemplateRunnerData.class, json);
        if (data == null) {
            return get404Response("No valid data json object given. TemplateRunnerData expected.");
        }
        log.info("Running template: definition=" + data.getTemplateDefinitionId() + ", template=" + data.getTemplateCanonicalPath());
        File file = new File(data.getTemplateCanonicalPath());
        if (!file.exists()) {
            return get404Response("Template file not found by canonical path: " + data.getTemplateCanonicalPath());
        }
        TemplateDefinition templateDefinition = Storage.getInstance().getTemplateDefinition(data.getTemplateDefinitionId());
        if (templateDefinition == null) {
            log.info("Template definition with id '" + data.getTemplateDefinitionId() + "' not found. Proceeding without template definition.");
        }
        Response response = null;
        try {
            WordTemplateRunner runner = new WordTemplateRunner(templateDefinition, new WordDocument(file));
            WordDocument result = runner.run(data.getVariables());
            //ZipUtil zipUtil = new ZipUtil("result.zip");
            //zipUtil.addZipEntry("result/" + file.getName(), result.getAsByteArrayOutputStream().toByteArray());
            Response.ResponseBuilder builder = Response.ok(result.getAsByteArrayOutputStream().toByteArray());
            builder.header("Content-Disposition", "attachment; filename=" + file.getName());
            // Needed to get the Content-Disposition by client:
            builder.header("Access-Control-Expose-Headers", "Content-Disposition");
            response = builder.build();
            log.info("Downloading file '" + file + "', length: " + file.length());
            return response;
        } catch (Exception ex) {
            String errorMsg = "Error while try to running template '" + data.getTemplateCanonicalPath() + "'.";
            log.error(errorMsg + " " + ex.getMessage(), ex);
            return get404Response(errorMsg);
        }
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
                            @QueryParam("templateDefinitionId") String templateDefinitionId,
                            @QueryParam("prettyPrinter") boolean prettyPrinter) {
        TemplateRunnerData data = new TemplateRunnerData();
        data.setTemplateDefinitionId(templateDefinitionId);
        data.setTemplateCanonicalPath(templateCanonicalPath);
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

    private Response get404Response(String errorMessage) {
        log.error(errorMessage);
        Response response = Response.status(404).
                entity(errorMessage).
                type("text/plain").
                build();
        return response;
    }
}
