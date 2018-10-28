package de.reinhard.merlin.utils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    private Logger log = LoggerFactory.getLogger(ZipUtils.class);
    private ZipOutputStream zipOut;
    private ByteArrayOutputStream outStream;
    private String filename;
    private Map<String, Integer> usedFilenames = new HashMap<>();

    public ZipUtils(String zipFilename) {
        this.filename = zipFilename;
        outStream = new ByteArrayOutputStream();
        zipOut = new ZipOutputStream(outStream);
    }

    /**
     * For multiple filenames, the filenames will be modified in a MacOS X style:
     * <tt>Filename.txt, Filename-2.txt, Filename-2.txt,</tt> ...
     *
     * @param filename
     * @param content
     */
    public void addZipEntry(String filename, byte[] content) {
        try {
            Integer counter = usedFilenames.get(filename);
            if (counter != null) {
                // Modify multiples filenames by adding counter (MacOS style).
                filename = FilenameUtils.getFullPath(filename)
                        + FilenameUtils.getBaseName(filename)
                        + ++counter + FilenameUtils.getExtension(filename);
            } else {
                counter = 1;
            }
            usedFilenames.put(filename, counter);
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

    public String getFilename() {
        return filename;
    }
}
