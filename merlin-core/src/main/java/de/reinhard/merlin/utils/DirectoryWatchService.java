package de.reinhard.merlin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatchService implements Runnable {
    private Logger log = LoggerFactory.getLogger(DirectoryWatchService.class);

    private final WatchService watchService;
    private final Map<WatchKey, WatchKeyEntry> watchKeyMap;
    private boolean doStop = false;

    /**
     * Creates a WatchService and registers the given directory
     */
    public DirectoryWatchService() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.watchKeyMap = new HashMap<WatchKey, WatchKeyEntry>();
    }

    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }

    /**
     * Process all events for watch keys queued to the watcher service.
     */
    @Override
    public void run() {
        while (true) {
            // wait for key to be signalled
            WatchKey eventWatchKey;
            try {
                // Retrieves and removes next watch key, waiting if none are yet present.
                eventWatchKey = this.watchService.take();
            } catch (InterruptedException x) {
                return;
            }
            WatchKeyEntry watchKeyEntry = watchKeyMap.get(eventWatchKey);
            Path dir = watchKeyEntry.path;
            if (dir == null) {
                log.error("Got watch key event, but it's not registered: " + eventWatchKey);
                continue;
            }
            for (WatchEvent<?> watchEvent : eventWatchKey.pollEvents()) {
                WatchEvent.Kind kind = watchEvent.kind();
                if (kind == OVERFLOW) {
                    log.warn("Got OVERFLOW, don't know how to proceed it: " + watchEvent.toString());
                    continue;
                }
                WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
                Path name = pathWatchEvent.context();
                Path child = dir.resolve(name);

                log.info("Got watch event '" + name + "' for child '" + child + "'");

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (watchKeyEntry.recursive && (kind == ENTRY_CREATE)) {
                    if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                        registerAll(child);
                    }
                }
            }
            // reset key and remove from set if directory no longer accessible
            boolean valid = eventWatchKey.reset();
            if (!valid) {
                watchKeyMap.remove(eventWatchKey);
                //if (watchKeyMap.isEmpty()) {
                    // all directories are inaccessible
                //    break;
                //}
            }
        }
    }

    public boolean register(Path dir, boolean recursive) {
        if (recursive) {
            return registerAll(dir);
        } else {
            return register(dir);
        }
    }

    /**
     * Register the given directory with the WatchService
     */
    private boolean register(Path dir) {
        WatchKey watchKey;
        try {
            watchKey = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException ex) {
            log.error("Can't register dir '" + dir + "': " + ex.getMessage(), ex);
            return false;
        }
        WatchKeyEntry watchKeyEntryEntry = watchKeyMap.get(watchKey);
        if (watchKeyEntryEntry == null) {
            log.info("Register '" + dir + "'.");
        } else {
            log.info("Already registered, updating '" + dir + "'.");
        }
        watchKeyMap.put(watchKey, watchKeyEntryEntry);
        return true;
    }

    /**
     * Register the given directory recursively.
     */
    private boolean registerAll(final Path start) {
        final boolean[] status = new boolean[1];
        status[0] = true;
        try {
            // Register directory recursively.
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            log.error("Can't register dir '" + start + "': " + ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    private class WatchKeyEntry {
        Path path;
        boolean recursive;

        WatchKeyEntry(Path path, boolean recursive) {
            this.path = path;
            this.recursive = recursive;
        }
    }
}
