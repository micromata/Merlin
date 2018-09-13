package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.javafx.FileBrowser;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Path("/files")
public class FilesServiceRest {
    private Logger log = LoggerFactory.getLogger(FilesServiceRest.class);
    private static final String TEST_OUT_DIR = "./out/";
    private static final String TEST_SRC_DIR = "./merlin-core/examples/tests/";

    // https://www.geekmj.org/jersey/jax-rs-multiple-files-upload-example-408/

/*    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

        String filePath = TEST_OUT_DIR + contentDispositionHeader.getFileName();

        // save the file to the server
        saveFile(fileInputStream, filePath);

        String output = "File upload OK.";
        return Response.status(200).entity(output).build();
    }*/

    @POST
    @Path("/upload-multi")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormDataMultiPart form) {

        FormDataBodyPart filePart = form.getField("file");

        ContentDisposition headerOfFilePart = filePart.getContentDisposition();

        InputStream fileInputStream = filePart.getValueAs(InputStream.class);

        String filePath = TEST_OUT_DIR + headerOfFilePart.getFileName();

        // save the file to the server
        saveFile(fileInputStream, filePath);
        String output = "File upload OK.";
        return Response.status(200).entity(output).build();
    }

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

    /**
     * Opens a file browser on the desktop app and returns the chosen dir. Works only if Browser and Desktop app are running
     * on the same host.
     * @return The chosen directory path (absolute path).
     */
    @GET
    @Path("/browse-dir")
    @Produces(MediaType.TEXT_PLAIN)
    public String browseDirectory(@Context HttpServletRequest requestContext) {
        log.info("browseDirectory called from: " + requestContext.getRemoteAddr());
        CompletableFuture<File> future = new CompletableFuture<>();
        FileBrowser.getInstance().open(FileBrowser.SelectFilter.DIRECTORY, future);
        File file = null;
        try {
            file = future.get(); // wait for future to be assigned a result and retrieve it
        } catch (InterruptedException | ExecutionException ex) {
            log.error("While waiting for file browser: " + ex.getMessage(), ex);
        }
        String result = file != null ? file.getAbsolutePath() : null;
        log.info("Director chosen: " + result);
        return result;
    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream, String serverLocation) {
        try {
            OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
            IOUtils.copy(uploadedInputStream, outpuStream);
        } catch (IOException e) {

            e.printStackTrace();
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
