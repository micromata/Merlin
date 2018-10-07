package de.reinhard.merlin.persistency;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation for watching a directory (e. g. in the filesystem) for modifications.
 */
public abstract class AbstractDirectoryWatcher {
    private Logger log = LoggerFactory.getLogger(AbstractDirectoryWatcher.class);

    protected Path root;
    protected String[] supportedFileExtensions;
    private Long lastCheck;
    private boolean recursive;
    private long refreshRateInMillis = 10000; // 10 seconds as default.
    private boolean dirty;
    private boolean recheckInProgress;

    private Map<Path, DirectoryWatchEntry> directoriesMap;
    private Map<Path, DirectoryWatchEntry> filesMap;
    private Map<Path, DirectoryWatchEntry> deletedDirectoriesMap;
    private Map<Path, DirectoryWatchEntry> deletedFilesMap;

    /**
     * @param root
     * @param supportedFileExtensions If null, all files will be proceeded, if given, only files with one of these extensions will
     *                                be proceeded. You may specify: {"docx", "xls", "xlsx"} as well as {".docx". ".xls", ".xlsx"}.
     */
    public AbstractDirectoryWatcher(Path root, boolean recursive, String... supportedFileExtensions) {
        this.root = root;
        this.recursive = recursive;
        this.supportedFileExtensions = supportedFileExtensions;
        this.directoriesMap = new HashMap<>();
        this.filesMap = new HashMap<>();
        this.deletedDirectoriesMap = new HashMap<>();
        this.deletedFilesMap = new HashMap<>();
    }

    private synchronized void walkTree() {
        log.debug("Walking through tree: " + root);
        long now = System.currentTimeMillis();
        DirectoryWatcherContext context = new DirectoryWatcherContext();
        walkTree(context);
        for (DirectoryWatchEntry entry : directoriesMap.values()) {
            if (!context.containsTouchedItem(entry.getPath())) {
                // Deleted directory found:
                entry.setType(ModificationType.DELETED);
                deletedDirectoriesMap.put(entry.getPath(), entry);
            }
        }
        for (DirectoryWatchEntry deletedEntry : deletedDirectoriesMap.values()) {
            log.debug("Delete dir: " + deletedEntry.getPath());
            directoriesMap.remove(deletedEntry.getPath());
        }
        for (DirectoryWatchEntry entry : filesMap.values()) {
            if (!context.containsTouchedItem(entry.getPath())) {
                // Deleted file found:
                entry.setType(ModificationType.DELETED);
                deletedFilesMap.put(entry.getPath(), entry);
            }
        }
        for (DirectoryWatchEntry entry : deletedFilesMap.values()) {
            log.debug("Delete file: " + entry.getPath());
            filesMap.remove(entry.getPath());
        }
        lastCheck = now;
    }

    /**
     * Remove all directories and file entries. Use this method only, if you want to re-read everything.
     *
     * @see #forceRecheck()
     */
    public synchronized void clear() {
        this.directoriesMap.clear();
        this.filesMap.clear();
        this.deletedFilesMap.clear();
        this.deletedDirectoriesMap.clear();
        lastCheck = null;
    }

    /**
     * Causes to walk through the tree independent on the last check time. The walkTree is called, if any variable
     * is accessed.
     *
     * @see #checkRefresh()
     */
    public void forceRecheck() {
        this.dirty = true;
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
        DirectoryWatchEntry existingEntry = getEntry(relPath);
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
        return !matches(path, supportedFileExtensions);
    }

    protected boolean matches(Path path, String... fileExtensions) {
        if (fileExtensions == null) {
            return true;
        }
        for (String extension : fileExtensions) {
            String str = extension.startsWith(".") ? extension.toLowerCase() : "." + extension.toLowerCase();
            if (path.getFileName().toString().toLowerCase().endsWith(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true, if the directory is scanned recursively (including sub directories) or not.
     */
    public boolean isRecursive() {
        return recursive;
    }

    public Long getLastCheck() {
        return lastCheck;
    }

    private Path getRelativePath(Path path) {
        if (path.isAbsolute()) {
            return root.relativize(path);
        }
        return path;
    }

    /**
     * Tries to get the DirectoryWatchEntry for this given path (directory or file).
     *
     * @param path
     * @return
     */
    public DirectoryWatchEntry getEntry(Path path) {
        Validate.notNull(path);
        checkRefresh();
        Path relPath = getRelativePath(path);
        DirectoryWatchEntry entry = this.directoriesMap.get(relPath);
        if (entry != null) {
            return entry;
        }
        entry = this.filesMap.get(relPath);
        if (entry != null) {
            return entry;
        }
        entry = this.deletedDirectoriesMap.get(relPath);
        if (entry != null) {
            return entry;
        }
        return this.deletedFilesMap.get(relPath);
    }

    /**
     * @param path
     * @param lastCheck
     * @return true, if the lastModified of the path is after lastCheck or if the path object is deleted.
     */
    public boolean isModified(Path path, long lastCheck) {
        checkRefresh();
        DirectoryWatchEntry entry = getEntry(path);
        if (entry == null || entry.getType() == ModificationType.DELETED) {
            return true;
        }
        return lastCheck < entry.getLastModified();
    }

    /**
     * Returning all files (not deleted ones), found by this watcher on last {@link #walkTree()}.
     *
     * @param absolutePath   If true, absolute pathes will be returned, otherwise relative pathes to the root directory of this watcher.
     * @param fileExtensions File extension to match. If null, all files will returned.
     * @return List might be empty.
     */
    public List<Path> listFiles(boolean absolutePath, String... fileExtensions) {
        checkRefresh();
        List<Path> files = new ArrayList<>();
        for (DirectoryWatchEntry entry : this.filesMap.values()) {
            if (!matches(entry.getPath(), fileExtensions)) {
                continue;
            }
            Path path = absolutePath ? getFullPath(entry.getPath()) : entry.getPath();
            files.add(path);
        }
        return files;
    }

    private Path getFullPath(Path path) {
        return root.resolve(path);
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


    /**
     * Checks weather to walkTree or not. If {@link #forceRecheck()} is called before or if the last check is
     * outdated, the walkTree is called.
     *
     * @see #walkTree()
     */
    protected synchronized void checkRefresh() {
        if (recheckInProgress) {
            return;
        }
        if (!dirty && lastCheck != null &&
                lastCheck + this.refreshRateInMillis > System.currentTimeMillis()) {
            return;
        }
        try {
            recheckInProgress = true;
            walkTree();
        } finally {
            recheckInProgress = false;
        }
    }
}
