package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.javafx.FileSystemBrowser;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.persistency.FileDescriptor;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;
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
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Path("/files")
public class FilesystemBrowserRest {
    private Logger log = LoggerFactory.getLogger(FilesystemBrowserRest.class);
    /**
     * Opens a directory browser or file browser on the desktop app and returns the chosen dir/file. Works only if Browser and Desktop app are running
     * on the same host.
     *
     * @param type    Supported values: "dir", "directory", "excel", "xls", "word", "doc", "file"
     * @param current The current path of file. If not given the directory/file browser starts with the last used directory or user.home.
     * @return The chosen directory path (absolute path).
     */
    @GET
    @Path("/browse-local-filesystem")
    @Produces(MediaType.APPLICATION_JSON)
    public String browseLocalFilesystem(@Context HttpServletRequest requestContext, @QueryParam("type") String type, @QueryParam("current") String current) {
        String msg = RestUtils.checkLocalDesktopAvailable(requestContext);
        if (msg != null) {
            return msg;
        }
        FileSystemBrowser.SelectFilter filter = FileSystemBrowser.getFilter(type);
        CompletableFuture<File> future = new CompletableFuture<>();
        File initialDirectory = null;
        if (StringUtils.isNotBlank(current)) {
            initialDirectory = FileSystemBrowser.getDirectory(new File(current));
        }
        FileSystemBrowser.getInstance().open(filter, initialDirectory, future);
        File file = null;
        try {
            file = future.get(); // wait for future to be assigned a result and retrieve it
        } catch (InterruptedException | ExecutionException ex) {
            log.error("While waiting for file browser: " + ex.getMessage(), ex);
        }
        FileSystemBrowser.getInstance().setLastDir(file);
        String result = "{\"directory\":\"" + (file != null ? file.getAbsolutePath() : "") + "\"}";
        return result;
    }

    /**
     * @return OK, if the local desktop services such as open file browser etc. are available.
     */
    @GET
    @Path("/local-fileservices-available")
    @Produces(MediaType.TEXT_PLAIN)
    public String browseLocalFilesystem(@Context HttpServletRequest requestContext) {
        String msg = RestUtils.checkLocalDesktopAvailable(requestContext);
        if (msg != null) {
            return msg;
        }
        return "OK";
    }

    /**
     * Works only if the client and the server are running on localhost and the Desktop
     *
     * @param primaryKey File to open on local file system.
     * @return OK, if the Desktop services are available and .
     */
    @GET
    @Path("/open-local-file")
    @Produces(MediaType.TEXT_PLAIN)
    public String openLocalFile(@Context HttpServletRequest requestContext, @QueryParam("primaryKey") String primaryKey) {
        if (StringUtils.isBlank(primaryKey)) {
            return "Can't open file, primaryKey required by this service.";
        }
        String msg = RestUtils.checkLocalDesktopAvailable(requestContext);
        if (msg != null) {
            return msg;
        }
        if (!Desktop.isDesktopSupported()) {
            msg = "Desktop service isn't supported.";
            log.error(msg);
            return msg;
        }
        java.nio.file.Path path;
        FileDescriptor fileDescriptor = null;
        TemplateDefinition templateDefinition = Storage.getInstance().getTemplateDefinition(primaryKey, true);
        if (templateDefinition != null) {
            fileDescriptor = templateDefinition.getFileDescriptor();
        } else {
            Template template = Storage.getInstance().getTemplate(primaryKey, true);
            if (template != null) {
                fileDescriptor = template.getFileDescriptor();
            }
        }
        if (fileDescriptor == null) {
            msg = "File represented by primaryKey '" + primaryKey + "' not found";
            log.error(msg);
            return msg;
        }
        File file = fileDescriptor.getCanonicalPath().toFile();
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        } catch (IOException ex) {
            log.error("Error while opening file '" + file.getAbsolutePath() + "'");
            return "Error while trying to open file '" + file.getAbsolutePath() + "'.";
        }
        return "OK";
    }
}
