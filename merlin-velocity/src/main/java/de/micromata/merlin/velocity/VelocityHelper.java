package de.micromata.merlin.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

public class VelocityHelper {
    private static Logger logger;

    static {
        Velocity.init();
        logger = LoggerFactory.getLogger(VelocityHelper.class);
    }

    public static void merge(File templateDir, String filename, File outSubDir, VelocityContext context) {
        merge(new File(templateDir, filename), new File(outSubDir, filename), context);
    }

    public static void merge(File templateFile, File outFile, VelocityContext context) {
        Template template = null;

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateFile.getParentFile().getAbsolutePath());

        String templatePath = templateFile.getAbsolutePath();
        logger.info("Processing template file: " + templatePath);
        try {
            template = ve.getTemplate(templateFile.getName());
        } catch (Exception ex) {
            logger.error("Couldn't find template '" + templatePath + "': " + ex.getMessage(), ex);
            return;
        }

        String outPath = outFile.getAbsolutePath();
        try (Writer fileWriter = new PrintWriter(outPath)) {
            logger.info("Writing file: " + outPath);
            template.merge(context, fileWriter);
        } catch (Exception ex) {
            logger.error("Can't open file '" + outPath + "': " + ex.getMessage(), ex);
            return;
        }
    }
}
