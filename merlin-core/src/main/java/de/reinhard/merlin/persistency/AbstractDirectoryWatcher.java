package de.reinhard.merlin.persistency;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * Abstract implementation for watching a directory (e. g. in the filesystem) for modifications.
 */
public abstract class AbstractDirectoryWatcher {
    private Logger log = LoggerFactory.getLogger(AbstractDirectoryWatcher.class);

    protected Path root;
    protected String[] fileExtensions;
    private Long lastCheck;

    private Map<Path, DirectoryWatchEntry> directoriesMap;
    private Map<Path, DirectoryWatchEntry> filesMap;
    private Set<Path> deletedDirectoriesSet;
    private Set<Path> deletedFilesSet;

    /**
     * @param root
     * @param fileExtensions If null, all files will be proceeded, if given, only files with one of these extensions will
     *                       be proceeded. You may specify: {"docx", "xls", "xlsx"} as well as {".docx". ".xls", ".xlsx"}.
     */
    public AbstractDirectoryWatcher(Path root, String... fileExtensions) {
        this.root = root;
        this.fileExtensions = fileExtensions;
        this.directoriesMap = new HashMap<>();
        this.filesMap = new HashMap<>();
        this.deletedDirectoriesSet = new HashSet<>();
        this.deletedFilesSet = new HashSet<>();
    }

    public synchronized void walkTree() {
        DirectoryWatcherContext context = new DirectoryWatcherContext();
        walkTree(context);
        for (DirectoryWatchEntry entry : directoriesMap.values()) {
            if (!context.containsTouchedItem(entry.getPath())) {
                // Deleted directory found:
                deletedDirectoriesSet.add(entry.getPath());
            }
        }
        for (Path path : deletedDirectoriesSet) {
            log.debug("Delete dir: " + path);
            directoriesMap.remove(path);
        }
        for (DirectoryWatchEntry entry : filesMap.values()) {
            if (!context.containsTouchedItem(entry.getPath())) {
                // Deleted file found:
                deletedFilesSet.add(entry.getPath());
            }
        }
        for (Path path : deletedFilesSet) {
            log.debug("Delete file: " + path);
            filesMap.remove(path);
        }
        lastCheck = System.currentTimeMillis();
    }

    /**
     * Remove all directory and file entries and re-walk the directory.
     */
    public synchronized void clear() {
        this.directoriesMap.clear();
        this.filesMap.clear();
        lastCheck = null;
        walkTree();
    }

    protected abstract void walkTree(DirectoryWatcherContext context);

    /**
     * @param path
     * @param lastModified the value in milliseconds, since the epoch (1970-01-01T00:00:00Z).
     */
    protected void visit(Path path, ItemType itemType, long lastModified, DirectoryWatcherContext context) {
        if (itemType == ItemType.DIR) {
            log.debug("Directory: " + path);
        } else {
            if (ignoreFile(path)) {
                log.debug("Ignoring file: " + path);
                return;
            }
            log.debug("File: " + path);
        }
        Path relPath = getRelativePath(path);
        context.add(relPath);
        DirectoryWatchEntry existingEntry = getEntry(relPath, itemType);
        if (lastCheck == null) {
            // Initial run.
            if (existingEntry != null) {
                log.warn("Oups, already processed, but it's the initial run: " + path + ". Skipping.");
                return;
            }
            existingEntry = new DirectoryWatchEntry(relPath, lastModified);
            putEntry(relPath, itemType, existingEntry);
            return;
        }
        if (existingEntry == null) {
            existingEntry = new DirectoryWatchEntry(relPath, lastModified, ModificationType.CREATED);
            putEntry(relPath, itemType, existingEntry);
            return;
        }
        if (lastModified > existingEntry.getLastModified()) {
            // Directory was modified after last run.
        }
        existingEntry.setLastModified(lastModified);
        putEntry(relPath, itemType, existingEntry);
    }

    /**
     * Checks by default the path for having one of the file extensions specified in the constructor (case-insensitive).
     *
     * @param path
     * @return
     */
    protected boolean ignoreFile(Path path) {
        if (fileExtensions == null) {
            return false;
        }
        for (String extension : fileExtensions) {
            String str = extension.startsWith(".") ? extension.toLowerCase() : "." + extension.toLowerCase();
            if (path.getFileName().toString().toLowerCase().endsWith(str)) {
                return false;
            }
        }
        return true;
    }

    public Set<Path> getDeletedDirectoriesSet() {
        return deletedDirectoriesSet;
    }

    public Set<Path> getDeletedFilesSet() {
        return deletedFilesSet;
    }

    public DirectoryWatchEntry getDirectoryEntry(Path path) {
        return directoriesMap.get(getRelativePath(path));
    }

    public DirectoryWatchEntry getFileEntry(Path path) {
        Path relPath = getRelativePath(path);
        return filesMap.get(relPath);
    }

    private Path getRelativePath(Path path) {
        if (path.isAbsolute()) {
            return root.relativize(path);
        }
        return path;
    }

    public DirectoryWatchEntry getEntry(Path path, ItemType type) {
        Validate.notNull(path);
        Validate.notNull(type);
        if (type == ItemType.DIR) {
            return getDirectoryEntry(path);
        } else {
            return getFileEntry(path);
        }
    }

    private void putEntry(Path path, ItemType type, DirectoryWatchEntry entry) {
        Validate.notNull(type);
        Validate.notNull(entry);
        Validate.notNull(path);
        if (type == ItemType.DIR) {
            this.directoriesMap.put(path, entry);
        } else {
            this.filesMap.put(path, entry);
        }
    }
}
