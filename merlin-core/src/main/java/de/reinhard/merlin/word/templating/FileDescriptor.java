package de.reinhard.merlin.word.templating;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.io.File;
import java.util.Date;

/**
 * Information about a file location. Used for auto-matching of template files and template definition files (if
 * the filenames are equal (including path, excluding file extension).
 * <br/>
 * If the template files and definition files aren't stored in a local file system this class can also be used.
 * Directory may represent the area, the relative path the sub area for clustering template files.
 */
public class FileDescriptor {
    private Logger log = LoggerFactory.getLogger(FileDescriptor.class);
    private String directory;
    private String relativePath;
    private String filename;
    private Date lastUpdate;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * Sets the property directory as absolute path of the given dir.
     *
     * @param dir
     * @return this for chaining.
     */
    public FileDescriptor setDirectory(File dir) {
        directory = dir.getAbsolutePath();
        return this;
    }

    /**
     * @return path relative to directory path (excluding filename). Needed for automatching template definition files with same
     * relative path and filename (excluding the file extension).
     */
    public String getRelativePath() {
        return relativePath;
    }

    public FileDescriptor setRelativePath(String relativePath) {
        this.relativePath = relativePath;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public FileDescriptor setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    /**
     * For syncing purposes: do only update if any modification of the representing file is newer than the last update.
     *
     * @return Date of last update of this object (if set).
     */
    @Transient
    public Date getLastUpdate() {
        return lastUpdate;
    }

    public FileDescriptor setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    /**
     * Checks if the filename matches the other filename (excluding the file extension).
     *
     * @param other
     * @return true, if the filenames matches and the files are both placed in the same directory (including relative path).
     */
    public boolean matches(FileDescriptor other) {
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

    /**
     * Checks weather the given file was modified after last update.
     *
     * @param file
     * @return true, if the gifen file was modified after last update or if last update is not set.
     */
    public boolean isModified(File file) {
        if (lastUpdate == null) {
            return true;
        }
        Date lastModified = new Date(file.lastModified());
        return lastModified.after(lastUpdate);
    }

    @Override
    public boolean equals(Object obj) {
        FileDescriptor other = (FileDescriptor) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(directory, other.directory);
        eb.append(relativePath, other.relativePath);
        eb.append(filename, other.filename);
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(directory).append(relativePath).append(filename);
        return hcb.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder tos = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tos.append("directory", directory);
        tos.append("relativePath", relativePath);
        tos.append("filename", filename);
        return tos.toString();
    }
}
