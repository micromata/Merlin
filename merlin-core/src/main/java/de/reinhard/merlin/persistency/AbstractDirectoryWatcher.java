package de.reinhard.merlin.persistency;

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
    protected String[] fileExtensions;
    private Long lastCheck;

    private Map<Path, DirectoryWatchEntry> directoriesMap;
    private Map<Path, DirectoryWatchEntry> filesMap;
    private List<DirectoryListener> listeners = new ArrayList<>();

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
    }

    public synchronized void walkTree() {
        walkTree(new DirectoryWatcherContext());
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
    protected void visitDirectory(Path path, long lastModified, DirectoryWatcherContext context) {
        log.debug("Directory: " + path);
        DirectoryWatchEntry existingEntry = directoriesMap.get(path);
        if (lastCheck == null) {
            // Initial run.
            if (existingEntry != null) {
                log.warn("Oups, already processed, but it's the initial run: " + path + ". Skipping.");
                return;
            }
            existingEntry = new DirectoryWatchEntry(path, lastModified);
            directoriesMap.put(path, existingEntry);
            return;
        }
        context.add(path);
        if (existingEntry == null) {
            existingEntry = new DirectoryWatchEntry(path, lastModified, ModificationType.CREATED);
            directoriesMap.put(path, existingEntry);
            notifyDirectoryEvent(root, path, lastModified, ModificationType.CREATED);
            return;
        }
        if (lastModified > this.lastCheck) {
            // Directory was modified after last run:
            notifyDirectoryEvent(root, path, lastModified, ModificationType.MODIFIED);
        }
        existingEntry.setLastModified(lastModified);
        directoriesMap.put(path, existingEntry);
    }

    /**
     * @param path
     * @param lastModified the value in milliseconds, since the epoch (1970-01-01T00:00:00Z).
     */
    protected void visitItem(Path path, long lastModified, DirectoryWatcherContext context) {
        if (ignoreFile(path)) {
            log.debug("Ignoring file: " + path);
            return;
        }
        log.debug("File: " + path);
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

    public void registerListener(DirectoryListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DirectoryListener listener) {
        listeners.remove(listener);
    }

    private void notifyDirectoryEvent(Path root, Path path, long lastModified, ModificationType type) {
        for (DirectoryListener listener : listeners) {
            listener.directoryEvent(root, path, lastModified, type);
        }
    }
}
