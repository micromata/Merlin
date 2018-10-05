package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.word.WordDocument;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import de.reinhard.merlin.word.templating.WordTemplateRunner;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.List;

@Path("/templates")
public class TemplateRunnerRest {
    private Logger log = LoggerFactory.getLogger(TemplateRunnerRest.class);

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
            return RestUtils.get404Response(log, "No valid data json object given. TemplateRunnerData expected.");
        }
        log.info("Running template: definition=" + data.getTemplateDefinitionId() + ", template=" + data.getTemplateCanonicalPath());
        File file = new File(data.getTemplateCanonicalPath());
        if (!file.exists()) {
            return RestUtils.get404Response(log, "Template file not found by canonical path: " + data.getTemplateCanonicalPath());
        }
        List<TemplateDefinition> templateDefinitions = Storage.getInstance().getTemplateDefinition(null, data.getTemplateDefinitionId());
        TemplateDefinition templateDefinition = null;
        if (CollectionUtils.isEmpty(templateDefinitions)) {
            log.info("Template definition with id '" + data.getTemplateDefinitionId() + "' not found. Proceeding without template definition.");
        } else {
            if (templateDefinitions.size() > 1) {
                log.warn("Multiple template definition files found with id '" + data.getTemplateDefinitionId() + "'.");
            }
            templateDefinition = templateDefinitions.get(0);
        }
        Response response = null;
        try {
            WordTemplateRunner runner = new WordTemplateRunner(templateDefinition, new WordDocument(file));
            WordDocument result = runner.run(data.getVariables());
            String filename = runner.createFilename(file.getName(), data.getVariables());
            //ZipUtil zipUtil = new ZipUtil("result.zip");
            //zipUtil.addZipEntry("result/" + file.getName(), result.getAsByteArrayOutputStream().toByteArray());
            Response.ResponseBuilder builder = Response.ok(result.getAsByteArrayOutputStream().toByteArray());
            builder.header("Content-Disposition", "attachment; filename=" + filename);
            // Needed to get the Content-Disposition by client:
            builder.header("Access-Control-Expose-Headers", "Content-Disposition");
            response = builder.build();
            log.info("Downloading file '" + filename + "', length: " + file.length());
            return response;
        } catch (Exception ex) {
            String errorMsg = "Error while try to run template '" + data.getTemplateCanonicalPath() + "'.";
            log.error(errorMsg + " " + ex.getMessage(), ex);
            return RestUtils.get404Response(log, errorMsg);
        }
    }
}
