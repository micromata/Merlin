package de.reinhard.merlin.persistency.templates;

import de.reinhard.merlin.persistency.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * Handle items (template files, template definition files and serial data files.
 */
abstract class AbstractHandler<T extends FileDescriptorInterface> {
    private Logger log = LoggerFactory.getLogger(AbstractHandler.class);

    private PersistencyInterface persistency = PersistencyRegistry.getDefault();
    private AbstractDirectoryWatcher directoryWatcher;
    protected DirectoryScanner directoryScanner;
    private String itemName;
    protected Map<Path, T> itemsMap = new HashMap<>();
    protected String[] supportedFileExtensions;

    AbstractHandler(DirectoryScanner directoryScanner, String itemName) {
        this.directoryScanner = directoryScanner;
        this.directoryWatcher = directoryScanner.getDirectoryWatcher();
        this.itemName = itemName;
    }

    void clear() {
        this.itemsMap.clear();
    }

    /**
     * Gets the item by path. If item or file is updated since last update, the item file
     * will be re-read.
     *
     * @param watchEntry
     * @return Found or read item, otherwise null.
     */
    T getItem(DirectoryWatchEntry watchEntry) {
        Date now = new Date();
        Path path = directoryWatcher.getCanonicalPath(watchEntry);
        FileDescriptor fileDescriptor = new FileDescriptor().setDirectory(directoryWatcher.getRootDir()).setRelativePath(path)
                .setLastUpdate(now);

        T item = getItem(fileDescriptor);
        if (item != null && !item.getFileDescriptor().isModified(path)) {
            log.debug("Skipping file '" + path + "'. It's not modified since last scan.");
            return item;
        }
        if (!persistency.exists(path)) {
            log.error("Can't read " + itemName + ". Path '" + path + "' doesn't exist.");
            return null;
        }
        if (!watchEntry.isSupportedItem() && !watchEntry.isModified(directoryWatcher)) {
            log.debug("Unsupported item '" + path + "' not modified. Skipping again.");
            return null;
        }

        log.info("Scanning file '" + path + "'.");
        item = read(watchEntry, path, fileDescriptor);
        if (item == null) {
            log.info("Skipping file '" + path.toAbsolutePath() + "', no " + itemName + " (OK).");
            watchEntry.setSupportedItem(false);
            return null;
        }
        item.setFileDescriptor(fileDescriptor);
        watchEntry.setSupportedItem(true);
        itemsMap.put(path, item);
        log.info("Valid Merlin " + itemName + ": " + path.toAbsolutePath());
        return item;
    }

    void checkAndRefreshItems() {
        // Check for new, deleted and updated files:
        List<DirectoryWatchEntry> watchEntries = directoryWatcher.listWatchEntries(true, supportedFileExtensions);
        for (DirectoryWatchEntry watchEntry : watchEntries) {
            T item = itemsMap.get(directoryWatcher.getCanonicalPath(watchEntry));
            if (item == null) {
                log.debug("Creating new " + itemName + ": " + directoryWatcher.getCanonicalPath(watchEntry));
                item = getItem(watchEntry);
            }
        }
        Iterator<Map.Entry<Path, T>> it = itemsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Path, T> entry = it.next();
            if (directoryWatcher.isDeleted(entry.getValue().getFileDescriptor().getCanonicalPath())) {
                log.debug("Remove deleted " + itemName + ": " + entry.getKey());
                itemsMap.remove(entry.getKey());
            }
        }
    }

    Collection<T> getItems() {
        return itemsMap.values();
    }

    T getItem(FileDescriptor descriptor) {
        for (T item : getItems()) {
            if (descriptor.equals(item.getFileDescriptor())) {
                return item;
            }
        }
        return null;
    }

    T getItem(String canonicalPath) {
        for (T item : getItems()) {
            if (canonicalPath.equals(item.getFileDescriptor().getCanonicalPathString())) {
                return item;
            }
        }
        return null;
    }

    abstract T read(DirectoryWatchEntry watchEntry, Path path, FileDescriptor fileDescriptor);
}
