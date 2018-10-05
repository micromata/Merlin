package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.word.templating.SerialDataExcelWriter;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Path("/templates")
public class TemplateSerialRunnerRest {
    private Logger log = LoggerFactory.getLogger(TemplateSerialRunnerRest.class);

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
                return RestUtils.get404Response(log, "Template with canonical path '" + templateCanonicalPath + "' not found.");
            }
            TemplateDefinition templateDefinition = null;
            if (StringUtils.isNotBlank(templateDefinitionId)) {
                List<TemplateDefinition> templateDefinitions = Storage.getInstance().getTemplateDefinition(null, templateDefinitionId);
                if (CollectionUtils.isEmpty(templateDefinitions)) {
                    return RestUtils.get404Response(log, "Template definition with id or name '" + templateDefinitionId + "' not found.");
                } else {
                    if (templateDefinitions.size() > 1) {
                        log.warn("Multiple template definitions found under id '" + templateDefinitionId + "'.");
                    }
                    templateDefinition = templateDefinitions.get(0);
                }
            }
            SerialDataExcelWriter writer = new SerialDataExcelWriter(null, null);
            ExcelWorkbook workbook = null;//writer.writeToWorkbook(templateDefinition, origSerialData);
            File file = new File("ContractSerialData.xlsx");
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
        } catch (
                Exception ex) {
            String errorMsg = "Error while try to create Excel template for a serial run for template '" + templateCanonicalPath + "'.";
            log.error(errorMsg + " " + ex.getMessage(), ex);
            return RestUtils.get404Response(log, errorMsg);
        }
    }
}
