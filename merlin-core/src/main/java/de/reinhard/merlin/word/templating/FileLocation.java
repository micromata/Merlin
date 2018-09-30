package de.reinhard.merlin.word.templating;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Information about a file location. Used for auto-matching of template files and template definition files (if
 * the filenames are equal (including path, excluding file extension).
 */
public class FileLocation {
    private Logger log = LoggerFactory.getLogger(FileLocation.class);
    private String directory;
    private String relativePath;
    private String filename;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * @return path relative to directory path (excluding filename). Needed for automatching template definition files with same
     * relative path and filename (excluding the file extension).
     */
    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Checks if the filename matches the other filename (excluding the file extension).
     *
     * @param other
     * @return true, if the filenames matches and the files are both placed in the same directory (including relative path).
     */
    public boolean matches(FileLocation other) {
        if (!StringUtils.equals(directory, other.directory)) {
            return false;
        }
        if (!StringUtils.equals(relativePath, other.relativePath)) {
            return false;
        }
        if (filename == null || other.filename == null) {
            return false;
        }
        String filenameWithoutExtension = FilenameUtils.removeExtension(filename).trim().toLowerCase();
        String otherFilenameWithoutExtension = FilenameUtils.removeExtension(other.filename).trim().toLowerCase();
        return filenameWithoutExtension.equals(otherFilenameWithoutExtension);
    }
}
