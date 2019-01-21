package de.micromata.merlin.persistency;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

public interface PersistencyInterface {
    /**
     * @param path The path to look for.
     * @return last modification of the given file or null, if no file or last modification is given.
     */
    public Long getLastModified(Path path);

    /**
     * @param path The path to look for.
     * @return the canonical path should specify this file / path bi-unique.
     */
    public String getCanonicalPathString(Path path);

    /**
     * Generates a primary key for the given path. The value must be bi-unique for the path.
     *
     * @param path The path to look for.
     * @return The primary key.
     */
    public String getPrimaryKey(Path path);


    /**
     * @param path The path to look for.
     * @return the value in milliseconds, since the epoch (1970-01-01T00:00:00Z)
     */
    public Path getCanonicalPath(Path path);

    /**
     * @param path The path to look for.
     * @return true, if the given path exists.
     */
    public boolean exists(Path path);

    /**
     * @param path The path to look for.
     * @return The input stream of the object (e. g. file) specified by the path.
     */
    public InputStream getInputStream(Path path);

    /**
     * @param path The path to look for.
     * @return File, if supported by the implementation, otherwise null (then use {@link #getInputStream(Path)} instead).
     */
    public File getFile(Path path);

    public AbstractDirectoryWatcher newInstance(Path root, boolean recursive, String... fileExtensions);
}
