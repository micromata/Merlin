package de.reinhard.merlin.persistency;

import java.nio.file.Path;

/**
 * Representing one entry of the directory for checking later changes.
 */
public class DirectoryWatchEntry {
    private Path path;
    private long lastModified;
    private ModificationType type;

    public DirectoryWatchEntry() {
    }

    public DirectoryWatchEntry(Path path, Long lastModified) {
        this.path = path;
        this.lastModified = lastModified;
    }

    public DirectoryWatchEntry(Path path, Long lastModified, ModificationType type) {
        this.path = path;
        this.lastModified = lastModified;
        this.type = type;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public ModificationType getType() {
        return type;
    }

    public void setType(ModificationType type) {
        this.type = type;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
