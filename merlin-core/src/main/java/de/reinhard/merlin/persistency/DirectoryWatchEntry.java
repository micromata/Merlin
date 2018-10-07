package de.reinhard.merlin.persistency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Representing one entry of the directory for checking later changes. May represent a directory or a file/item.
 */
public class DirectoryWatchEntry {
    private Logger log = LoggerFactory.getLogger(DirectoryWatchEntry.class);

    private Path path;
    private long lastModified;
    private ModificationType type;
    private boolean supportedItem;

    public DirectoryWatchEntry(Path path, Long lastModified) {
        this(path, lastModified, null);
    }

    public DirectoryWatchEntry(Path path, Long lastModified, ModificationType type) {
        this.path = path;
        this.lastModified = lastModified;
        this.type = type;
        this.supportedItem = true;
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

    public void setSupportedItem(boolean supportedItem) {
        this.supportedItem = supportedItem;
    }

    /**
     * For marking items / files as unsupported. Leave them untouched (until any modification of such files is done).
     *
     * @return
     */
    public boolean isSupportedItem() {
        return supportedItem;
    }

    public boolean isModified(AbstractDirectoryWatcher watcher) {
        Long itemModified = PersistencyRegistry.getDefault().getLastModified(watcher.getCanonicalPath(this));
        if (itemModified == null) {
            return true;
        }
        return itemModified > lastModified;
    }
}
