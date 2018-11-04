package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.logging.MDCHandler;
import de.reinhard.merlin.logging.MDCKey;
import de.reinhard.merlin.persistency.PersistencyRegistry;
import de.reinhard.merlin.utils.MerlinFileUtils;
import de.reinhard.merlin.word.WordDocument;
import de.reinhard.merlin.word.templating.*;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/files")
public class FileUploadRest {
    private Logger log = LoggerFactory.getLogger(FileUploadRest.class);

    // https://www.geekmj.org/jersey/jax-rs-multiple-files-upload-example-408/

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormDataMultiPart form) {
        MDCHandler mdc = new MDCHandler();
        try {
            FormDataBodyPart filePart = form.getField("file");
            ContentDisposition headerOfFilePart = filePart.getContentDisposition();
            InputStream fileInputStream = filePart.getValueAs(InputStream.class);
            String filename = headerOfFilePart.getFileName();
            if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                ExcelWorkbook workbook = new ExcelWorkbook(fileInputStream, filename);
                if (SerialDataExcelReader.isMerlinSerialRunDefinition(workbook)) {
                    return runSerial(filename, workbook, mdc);
                }
            }
            log.info("Unsupported file:" + filename);
            String output = "Unsupported file.";
            return Response.status(Response.Status.BAD_REQUEST).entity(output).build();
        } finally {
            mdc.restore();
        }
    }


    private Response runSerial(String filename, ExcelWorkbook workbook, MDCHandler mdc) {
        log.info("Processing Merlin Serial Definition file: " + filename);
        SerialDataExcelReader reader = new SerialDataExcelReader(workbook);
        SerialData serialData = reader.getSerialData();
        String templateDefinitionPrimaryKey = serialData.getReferencedTemplateDefinitionPrimaryKey();
        TemplateDefinition templateDefinition = null;
        if (StringUtils.isNotBlank(templateDefinitionPrimaryKey)) {
            templateDefinition = Storage.getInstance().getTemplateDefinition(templateDefinitionPrimaryKey);
            if (templateDefinition != null) {
                mdc.put(MDCKey.TEMPLATE_DEFINITION_PK, templateDefinitionPrimaryKey);
            }
            serialData.setTemplateDefinition(templateDefinition);
        }
        String templatePrimaryKey = serialData.getReferencedTemplatePrimaryKey();
        if (StringUtils.isBlank(templatePrimaryKey)) {
            return RestUtils.get404Response(log, "No template specified. Abort serial processing...");
        }
        Template template = Storage.getInstance().getTemplate(templatePrimaryKey);
        if (template == null) {
            return RestUtils.get404Response(log, "Can't load template '\" + templatePrimaryKey + \"'. Abort serial processing...");

        }
        serialData.setTemplate(template);

        mdc.put(MDCKey.TEMPLATE_PK, templatePrimaryKey);
        java.nio.file.Path path = template.getFileDescriptor().getCanonicalPath();
        if (!PersistencyRegistry.getDefault().exists(path)) {
            return RestUtils.get404Response(log, "Template file not found by canonical path: " + path);
        }
        WordDocument templateDocument = WordDocument.load(path);
        SerialTemplateRunner runner = new SerialTemplateRunner(serialData, templateDocument);
        reader.readVariables(serialData.getTemplate().getStatistics());
        byte[] zipByteArray = runner.run(filename);
        Response.ResponseBuilder builder = Response.ok(zipByteArray);
        builder.header("Content-Disposition", "attachment; filename=" + runner.getZipFilename());
        // Needed to get the Content-Disposition by client:
        builder.header("Access-Control-Expose-Headers", "Content-Disposition");
        Response response = builder.build();
        log.info("Downloading file '" + runner.getZipFilename() + "', length: " + MerlinFileUtils.getByteCountToDisplaySize(zipByteArray.length));
        return response;
    }
}
