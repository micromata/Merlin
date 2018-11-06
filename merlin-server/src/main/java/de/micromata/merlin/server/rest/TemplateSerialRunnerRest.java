package de.micromata.merlin.server.rest;

import de.micromata.merlin.server.storage.Storage;
import de.micromata.merlin.excel.ExcelWorkbook;
import de.micromata.merlin.logging.MDCHandler;
import de.micromata.merlin.logging.MDCKey;
import de.micromata.merlin.word.templating.SerialData;
import de.micromata.merlin.word.templating.SerialDataExcelWriter;
import de.micromata.merlin.word.templating.Template;
import de.micromata.merlin.word.templating.TemplateDefinition;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/templates")
public class TemplateSerialRunnerRest {
    private Logger log = LoggerFactory.getLogger(TemplateSerialRunnerRest.class);

    @GET
    @Path("get-serial-run-excel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    /**
     * Generates a Excel file for filling out variables for a serial run for the given template and optional template definition.
     * @param requestContext For getting client locale.
     * @param templatePrimaryKey Primary key of the template to run.
     * @param templateDefinitionPrimaryKey Primary key of the template definition (if exist)
     */
    public Response getExampleSerialRundata(@Context HttpServletRequest requestContext,
                                            @QueryParam("templatePrimaryKey") String templatePrimaryKey,
                                            @QueryParam("templateDefinitionPrimaryKey") String templateDefinitionPrimaryKey) {
        log.info("Getting Excel template for serial run: template=" + templatePrimaryKey + ", templateDefinition="
                + templatePrimaryKey);
        Response response = null;
        MDCHandler mdc = new MDCHandler();
        try {
            mdc.put(MDCKey.TEMPLATE_PK, templatePrimaryKey);
            Template template = Storage.getInstance().getTemplate(templatePrimaryKey);
            if (template == null) {
                return RestUtils.get404Response(log, "Template with primary key '" + templatePrimaryKey + "' not found.");
            }
            TemplateDefinition templateDefinition = null;
            if (StringUtils.isNotBlank(templateDefinitionPrimaryKey)) {
                templateDefinition = Storage.getInstance().getTemplateDefinition(templateDefinitionPrimaryKey);
                if (templateDefinition == null) {
                    log.error("Template definition with primary key or id '" + templateDefinitionPrimaryKey + "' not found.");
                } else {
                    mdc.put(MDCKey.TEMPLATE_DEFINITION_PK, templateDefinitionPrimaryKey);
                }
            }
            SerialData serialData = new SerialData();
            serialData.setTemplate(template);
            serialData.setTemplateDefinition(templateDefinition);
            SerialDataExcelWriter writer = new SerialDataExcelWriter(serialData);
            writer.getTemplateRunContext().setLocale(RestUtils.getUserLocale(requestContext));
            ExcelWorkbook workbook = writer.writeToWorkbook();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.getPOIWorkbook().write(bos);

            String filename = serialData.createFilenameForSerialTemplate();
            Response.ResponseBuilder builder = Response.ok(bos.toByteArray());
            builder.header("Content-Disposition", "attachment; filename=" + filename);
            // Needed to get the Content-Disposition by client:
            builder.header("Access-Control-Expose-Headers", "Content-Disposition");
            response = builder.build();
            log.info("Downloading file '" + filename + "', length: " + bos.size());
            return response;
        } catch (Exception ex) {
            String errorMsg = "Error while try to create Excel serial run template.";
            log.error(errorMsg + " " + ex.getMessage(), ex);
            return RestUtils.get404Response(log, errorMsg);
        } finally {
            mdc.restore();
        }
    }
}
