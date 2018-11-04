package de.reinhard.merlin.app.rest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.*;

@Path("/files")
public class FilesServiceRest {
    private Logger log = LoggerFactory.getLogger(FilesServiceRest.class);
    private static final String TEST_OUT_DIR = "./merlin-core/out/";
    private static final String TEST_SRC_DIR = "./merlin-core/examples/tests/";

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFilebyQuery(@QueryParam("filename") String fileName) {
        return download(fileName);
    }

    @GET
    @Path("/download/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFilebyPath(@PathParam("filename") String fileName) {
        return download(fileName);
    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream, File dir, String filename) {
        try {
            if (!dir.exists()) {
                dir.createNewFile();
            }
            File file = new File(dir, filename);
            log.info("Writing file '" + file.getAbsolutePath() + "'");
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream outpuStream = new FileOutputStream(file);
            IOUtils.copy(uploadedInputStream, outpuStream);
        } catch (IOException ex) {
            log.error("Can't write file '" + filename + "': " + ex.getMessage());
        }
    }

    private Response download(String fileName) {
        log.info("Downloading file: " + fileName);
        String fileLocation = TEST_SRC_DIR + "Test.xlsx";
        Response response = null;

        File file = new File(fileLocation);
        log.info(file.getAbsolutePath());
        if (file.exists()) {
            ResponseBuilder builder = Response.ok(file);
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
}
