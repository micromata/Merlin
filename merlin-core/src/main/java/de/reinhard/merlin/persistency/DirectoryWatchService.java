package de.reinhard.merlin.persistency;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatchService implements Runnable {
    private Logger log = LoggerFactory.getLogger(DirectoryWatchService.class);

    private WatchService watchService;
    private Map<WatchKey, WatchKeyEntry> watchKeyMap;
    private boolean doStop = false;

    /**
     * Creates a WatchService and registers the given directory
     */
    public DirectoryWatchService() {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            log.error("Can't instantiate DirectoryWatchService: " + ex.getMessage(), ex);
        }
        this.watchKeyMap = new HashMap<>();
    }

    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                try {
                    DirectoryWatchService.this.run();
                } catch (Exception ex) {
                    log.error("Exception in thread running DirectoryWatchService: " + ex.getMessage(), ex);
                }
            }
        }.start();
    }

    /**
     * Process all events for watch keys queued to the watcher service.
     */
    @Override
    public void run() {
        log.info("Running watch service.");
        try {
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
                if (watchKeyEntry == null) {
                    log.error("Got watch key event, but it's not registered: " + watchKeyToString(eventWatchKey));
                    continue;
                }
                Path dir = watchKeyEntry.path;
                for (WatchEvent<?> watchEvent : eventWatchKey.pollEvents()) {
                    WatchEvent.Kind kind = watchEvent.kind();
                    if (kind == OVERFLOW) {
                        log.warn("Got OVERFLOW, don't know how to proceed it: " + watchEvent.toString());
                        continue;
                    }
                    WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
                    Path name = pathWatchEvent.context();
                    Path child = dir.resolve(name);

                    log.info("Got watch event of kind '" + kind + "' for child '" + child + "'");

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
        } finally {
            log.warn("Stopping watch service.");
        }
    }

    public boolean register(Path dir, boolean recursive) {
        if (recursive) {
            return registerAll(dir);
        } else {
            return _register(dir, recursive);
        }
    }

    /**
     * Register the given directory with the WatchService
     */
    private boolean _register(Path path, boolean recursive) {
        path = PersistencyRegistry.getDefault().getCanonicalPath(path);
        //if (path.is)
        WatchKey watchKey;
        try {
            //watchKey = path.register(watchService,  new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW},
            //        SensitivityWatchEventModifier.HIGH);
            watchKey = path.register(watchService,  ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
        } catch (IOException ex) {
            log.error("Can't register dir '" + path + "': " + ex.getMessage(), ex);
            return false;
        }
        WatchKeyEntry watchKeyEntry = watchKeyMap.get(watchKey);
        if (watchKeyEntry == null) {
            log.info("Register '" + path + "'.");
            watchKeyEntry = new WatchKeyEntry(path, recursive);
        } else {
            log.info("Already registered, updating '" + path + "'.");
        }
        watchKeyMap.put(watchKey, watchKeyEntry);
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
                    _register(dir, true);
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

    private String watchKeyToString(WatchKey watchKey) {
        ToStringBuilder tos = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        List<WatchEvent<?>> events = watchKey.pollEvents();
        if (events != null) {
            for (WatchEvent event : events) {
                ToStringBuilder tos2 = new ToStringBuilder(event, ToStringStyle.SHORT_PREFIX_STYLE);
                tos.append("kind", event.kind());
                tos.append("context", event.context());
                tos.append("count", event.count());
                tos.append("event", tos2.toString());
            }
        }
        return tos.toString();
    }
}
