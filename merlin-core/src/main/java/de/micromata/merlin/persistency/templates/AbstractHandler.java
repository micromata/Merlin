package de.micromata.merlin.persistency.templates;

import de.micromata.merlin.logging.MDCHandler;
import de.micromata.merlin.logging.MDCKey;
import de.micromata.merlin.persistency.*;
import de.micromata.merlin.utils.I18nLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * Handle items (template files, template definition files and serial data files.
 */
abstract class AbstractHandler<T extends FileDescriptorInterface> {
    private Logger log = LoggerFactory.getLogger(AbstractHandler.class);
    private static final int MAX_REFRESH_RATE_MILLIS = 5000; // Refresh only every 5 seconds.

    private PersistencyInterface persistency = PersistencyRegistry.getDefault();
    private AbstractDirectoryWatcher directoryWatcher;
    protected DirectoryScanner directoryScanner;
    private String itemName;
    protected Map<Path, T> itemsMap = new HashMap<>();
    protected String[] supportedFileExtensions;
    // Stores the time of last check. If unsupported files are modified, they will be checked for Merlin files again in walkTree.
    private Map<Path, Long> unsupportedFilesMap = new HashMap<>();
    private long lastRefresh = -1;

    AbstractHandler(DirectoryScanner directoryScanner, String itemName) {
        this.directoryScanner = directoryScanner;
        this.directoryWatcher = directoryScanner.getDirectoryWatcher();
        this.itemName = itemName;
    }

    void clear() {
        this.itemsMap.clear();
        this.unsupportedFilesMap.clear();
    }

    /**
     * Gets the item by path. If item or file is updated since last update, the item file
     * will be re-read.
     *
     * @param watchEntry
     * @return Found or read item, otherwise null.
     */
    void processItem(DirectoryWatchEntry watchEntry) {
        Date now = new Date();
        Path path = directoryWatcher.getCanonicalPath(watchEntry);
        FileDescriptor fileDescriptor = new FileDescriptor().setDirectory(directoryWatcher.getRootDir()).setRelativePath(path).setLastUpdate(now);
        T item = getItem(fileDescriptor);
        if (item != null && !item.getFileDescriptor().isModified(path)) {
            log.debug("Skipping file '" + path + "'. It's not modified since last scan.");
            return;
        }
        if (!persistency.exists(path)) {
            log.error("Can't read " + itemName + ". Path '" + path + "' doesn't exist.");
            return;
        }
        if (!watchEntry.isSupportedItem()) {
            Long lastCheck = unsupportedFilesMap.get(watchEntry.getPath());
            if (lastCheck == null) {
                log.error("lastCheck shouldn't be null, path: " + path);
            } else if (watchEntry.getLastModified() < lastCheck) {
                log.debug("Unsupported item '" + path + "' not modified. Skipping again.");
                return;
            }
        }
        log.info(I18nLogger.get("merlin.log.word.templating.scanning", path));
        //long currentMillis = System.currentTimeMillis();
        item = read(watchEntry, path, fileDescriptor);
        //log.info("Time of scanning: " + (System.currentTimeMillis() - currentMillis)  + "ms.");
        if (item == null) {
            log.info(I18nLogger.get("merlin.log.word.templating.skipping." + itemName, path.toAbsolutePath()));
            watchEntry.setSupportedItem(false);
            unsupportedFilesMap.put(watchEntry.getPath(), now.getTime());
            return;
        }
        MDCHandler mdc = new MDCHandler();
        try {
            mdc.put(getMDCKey(), fileDescriptor.getPrimaryKey());
            if (item.getFileDescriptor() == null) {
                item.setFileDescriptor(fileDescriptor);
            }
            watchEntry.setSupportedItem(true);
            itemsMap.put(path, item);
            log.info(I18nLogger.get("merlin.log.word.templating.valid_merlin_item_found." + itemName, path.toAbsolutePath()));
        } finally {
            mdc.restore();
        }
    }

    synchronized void checkAndRefreshItems() {
        long now = System.currentTimeMillis();
        if (now < lastRefresh + MAX_REFRESH_RATE_MILLIS) {
            return;
        }
        // Check for new, deleted and updated files:
        List<DirectoryWatchEntry> watchEntries = directoryWatcher.listWatchEntries(true, supportedFileExtensions);
        for (DirectoryWatchEntry watchEntry : watchEntries) {
            if (watchEntry.getType() == ModificationType.DELETED) {
                Path path = directoryWatcher.getCanonicalPath(watchEntry);
                if (persistency.exists(path)) {
                    log.info(itemName + " undeleted: " + path);
                    watchEntry.setType(ModificationType.CREATED);
                    processItem(watchEntry);
                }
            } else {
                processItem(watchEntry);
            }
        }
        Iterator<Map.Entry<Path, T>> it = itemsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Path, T> entry = it.next();
            FileDescriptor fileDescriptor = entry.getValue().getFileDescriptor();
            if (directoryWatcher.isDeleted(fileDescriptor.getCanonicalPath())) {
                MDCHandler mdc = new MDCHandler();
                try {
                    entry.getValue();
                    mdc.put(getMDCKey(), fileDescriptor.getPrimaryKey());
                    log.debug("Remove deleted " + itemName + ": " + entry.getKey());
                    it.remove();
                } finally {
                    mdc.restore();
                }
            }
        }
        lastRefresh = now;
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

    T getItem(String primaryKey) {
        for (T item : getItems()) {
            if (primaryKey.equals(item.getFileDescriptor().getPrimaryKey())) {
                return item;
            }
        }
        return null;
    }

    abstract T read(DirectoryWatchEntry watchEntry, Path path, FileDescriptor fileDescriptor);

    /**
     * Each item action should be logged with the primary key of the item stored in the log's MDC.
     * @return
     */
    protected MDCKey getMDCKey() {
        return null;
    }
}
