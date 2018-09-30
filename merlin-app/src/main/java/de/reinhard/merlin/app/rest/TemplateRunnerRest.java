package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
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
        log.info("Running template: definition=" + data.getTemplateDefinitionId() + ", template=" + data.getTemplateId());
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
    public String getConfig(@QueryParam("prettyPrinter") boolean prettyPrinter) {
        TemplateRunnerData data = new TemplateRunnerData();
        data.setTemplateDefinitionId(Storage.getInstance().getTemplateDefinitions().get(0).getId());
        data.setTemplateId("ContractTemplate.docx");
        data.put("Gender", "female");
        data.put("Employee", "Berta Smith");
        data.put("Date", new Date());
        data.put("WeeklyHours", 40);
        data.put("NumberOfLeaveDays", 30);
        String json = JsonUtils.toJson(data, prettyPrinter);
        return json;
    }


}
