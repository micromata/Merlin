package de.reinhard.merlin.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private Logger log = LoggerFactory.getLogger(ZipUtil.class);
    private ZipOutputStream zipOut;
    private ByteArrayOutputStream outStream;
    private String filename;

    public ZipUtil(String zipFilename) {
        this.filename = zipFilename;
        outStream = new ByteArrayOutputStream();
        zipOut = new ZipOutputStream(outStream);
    }

    public void addZipEntry(String filename, byte[] content) {
        try {
            ZipEntry zipEntry = new ZipEntry(filename);
            zipOut.putNextEntry(zipEntry);
            zipOut.write(content);
        } catch (IOException ex) {
            log.error("Can't add zipEntry: '" + filename + "': " + ex.getMessage(), ex);
        }
    }

    public byte[] closeAndGetByteArray() {
        try {
            zipOut.close();
        } catch (IOException ex) {
            log.error("Can't close zip archive: '" + filename + "': " + ex.getMessage(), ex);
        }
        return outStream.toByteArray();
    }
}
