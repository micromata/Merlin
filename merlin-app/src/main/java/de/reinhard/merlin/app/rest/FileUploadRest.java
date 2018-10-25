package de.reinhard.merlin.app.rest;

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

        FormDataBodyPart filePart = form.getField("file");

        ContentDisposition headerOfFilePart = filePart.getContentDisposition();

        InputStream fileInputStream = filePart.getValueAs(InputStream.class);

        String filename = headerOfFilePart.getFileName();

        // save the file to the server
        //saveFile(fileInputStream, new File(TEST_OUT_DIR), headerOfFilePart.getFileName());
        String output = "File upload OK.";
        return Response.status(200).entity(output).build();
    }
}
