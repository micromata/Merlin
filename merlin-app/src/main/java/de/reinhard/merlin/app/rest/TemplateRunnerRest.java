package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.word.WordDocument;
import de.reinhard.merlin.word.templating.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;

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
            return get404Response(errorMsg);
        }
    }

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
        data.setTemplateCanonicalPath(exampleData.templateCanonicalPath);
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
    @Path("serial-run-excel-template")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    /**
     *
     * @param templateCanonicalPath Canonical path of the template to run.
     * @param templateDefinitionId Id or name of the template defintion (if exist)
     * @see JsonUtils#toJson(Object, boolean)
     */
    public Response getExampleSerialRundata(@QueryParam("templateCanonicalPath") String templateCanonicalPath,
                                          @QueryParam("templateDefinitionId") String templateDefinitionId) {
        log.info("Getting Excel template for serial run: template=" + templateCanonicalPath + ", templateDefinition="
                + templateDefinitionId);
        Response response = null;
        try {
            Template template = Storage.getInstance().getTemplate(templateCanonicalPath);
            if (template == null) {
                return get404Response("Template with canonical path '" + templateCanonicalPath + "' not found.");
            }
            TemplateDefinition templateDefinition = null;
            if (StringUtils.isNotBlank(templateDefinitionId)) {
                 templateDefinition = Storage.getInstance().getTemplateDefinition(templateDefinitionId);
                if (templateDefinition == null) {
                    return get404Response("Template definition with id or name '" + templateDefinitionId + "' not found.");
                }
            }
            SerialDataExcelWriter writer = new SerialDataExcelWriter(null);
            ExcelWorkbook workbook = null;//writer.writeToWorkbook(templateDefinition, origSerialData);
            File file = new File( "ContractSerialData.xlsx");
            log.info("Writing modified Excel file: " + file.getAbsolutePath());
            workbook.getPOIWorkbook().write(new FileOutputStream(file));

            String filename = "serial.xlsx";//runner.createFilename(file.getName(), data.getVariables());
            Response.ResponseBuilder builder = Response.ok(null);//result.getAsByteArrayOutputStream().toByteArray());
            builder.header("Content-Disposition", "attachment; filename=" + filename);
            // Needed to get the Content-Disposition by client:
            builder.header("Access-Control-Expose-Headers", "Content-Disposition");
            response = builder.build();
            //log.info("Downloading file '" + filename + "', length: " + file.length());
            return response;
        } catch (Exception ex) {
            String errorMsg = "Error while try to create Excel template for a serial run for template '" + templateCanonicalPath + "'.";
            log.error(errorMsg + " " + ex.getMessage(), ex);
            return get404Response(errorMsg);
        }
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
        return data;
    }

    public static class ExampleData {
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
