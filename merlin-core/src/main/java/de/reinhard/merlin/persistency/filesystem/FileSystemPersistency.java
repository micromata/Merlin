package de.reinhard.merlin.persistency.filesystem;

import de.reinhard.merlin.persistency.AbstractDirectoryWatcher;
import de.reinhard.merlin.persistency.PersistencyInterface;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class FileSystemPersistency implements PersistencyInterface {
    private Logger log = LoggerFactory.getLogger(FileSystemPersistency.class);

    public Long getLastModified(Path path) {
        File file = path.toFile();
        if (!file.exists()) {
            log.error("Can't check last modification of file '" + getCanonicalPath(path) + "'. It doesn't exist.");
            return null;
        }
        try {
            FileTime fileTime = Files.getLastModifiedTime(path);
            if (fileTime != null) {
                return fileTime.toMillis();
            }
        } catch (IOException ex) {
            log.error("Can't check last modification of file '" + getCanonicalPath(path) + "': " + ex.getMessage(), ex);
        }
        return System.currentTimeMillis();
    }

    public Path getCanonicalPath(Path path) {
        if (path == null) {
            return null;
        }
        return Paths.get(getCanonicalPathString(path));
    }

    public String getCanonicalPathString(Path path) {
        File file = path.toFile();
        try {
            return file.getCanonicalPath();
        } catch (IOException ex) {
            log.error("Can't get canonical path for '" + path + "': " + ex.getMessage(), ex);
            return file.getAbsolutePath();
        }
    }

    /**
     * Builds hash id (MD5, base64 and url encoded) from the canonical path. If SHA256 is not available, {@link String#hashCode()}
     * will be used instead as a fall back.<br/>
     * Security nodes: We need a fast hash algorithm, MD5 is not secure, but for this purpose the security lack of MD5 should
     * have no negative effect.
     * @param path
     * @return
     * @see #getCanonicalPathString(Path)
     */
    @Override
    public String getPrimaryKey(Path path) {
        String canonicalPath = getCanonicalPathString(path);
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] encodedhash = digest.digest(canonicalPath.getBytes(StandardCharsets.UTF_8));
            String base64 = Base64.getEncoder().encodeToString(encodedhash);
            return StringUtils.replaceChars(base64, "+/=", "._-");
        } catch (NoSuchAlgorithmException ex) {
            log.error("SHA-256 not available (falling back to hashCode): " + ex.getMessage(), ex);
            return String.valueOf(canonicalPath.hashCode());
        }
    }

    public boolean exists(Path path) {
        File file = path.toFile();
        return file.exists();
    }

    /**
     * @param path
     * @return The file input stream.
     */
    public InputStream getInputStream(Path path) {
        File file = path.toFile();
        if (!file.exists()) {
            log.error("Can't create input stream from unexisting path '" + path.toAbsolutePath() + "'.");
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (IOException ex) {
            log.error("Can't create input stream from path '" + path.toAbsolutePath() + "': " + ex.getMessage(), ex);
            return null;
        }
    }

    public File getFile(Path path) {
        return path.toFile();
    }

    @Override
    public AbstractDirectoryWatcher newInstance(Path root, boolean recursive, String... fileExtensions) {
        return new FileSystemDirectoryWatcher(root, recursive, fileExtensions);
    }
}
