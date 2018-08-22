package de.reinhard.merlin.velocity;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class VelocityHelper {
    private static Logger logger;

    static {
        Velocity.init();
        logger = LoggerFactory.getLogger(VelocityHelper.class);
    }

    public static void merge(String filename, File outSubDir, VelocityContext context) {
        Template template = null;

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, new File("data").getAbsolutePath());

        File templateFile = new File("data", filename);
        String templatePath = templateFile.getAbsolutePath();
        logger.info("Processing template file: " + templatePath);
        try {
            template = ve.getTemplate(filename);
        } catch (Exception ex) {
            logger.error("Couldn't find template '" + templatePath + "': " + ex.getMessage(), ex);
            return;
        }

        File out = new File(outSubDir, filename);
        String outPath = out.getAbsolutePath();
        Writer fileWriter = null;
        try {
            try {
                fileWriter = new PrintWriter(outPath);
            } catch (FileNotFoundException ex) {
                logger.error("Can't open file '" + outPath + "': " + ex.getMessage(), ex);
                return;
            }
            logger.info("Writing config file: " + outPath);
            template.merge(context, fileWriter);
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }
}
