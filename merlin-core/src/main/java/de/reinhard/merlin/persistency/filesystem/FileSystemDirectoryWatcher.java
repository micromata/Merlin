package de.reinhard.merlin.persistency.filesystem;

import de.reinhard.merlin.persistency.AbstractDirectoryWatcher;
import de.reinhard.merlin.persistency.DirectoryWatcherContext;
import de.reinhard.merlin.persistency.ItemType;
import de.reinhard.merlin.persistency.PersistencyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Implementation for watching a directory in the filesystem for modifications.
 */
public class FileSystemDirectoryWatcher extends AbstractDirectoryWatcher {
    private Logger log = LoggerFactory.getLogger(FileSystemDirectoryWatcher.class);

    public FileSystemDirectoryWatcher(Path root, String... fileExtensions) {
        super(PersistencyRegistry.getDefault().getCanonicalPath(root), fileExtensions);
    }

    @Override
    protected synchronized void walkTree(DirectoryWatcherContext context) {
        try {
            log.debug("Walking through the file system: " + root);
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    long lastModified = attrs.lastModifiedTime().toMillis();
                    visit(dir, ItemType.DIR, lastModified, context);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    long lastModified = attrs.lastModifiedTime().toMillis();
                    visit(file, ItemType.FILE, lastModified, context);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            log.error("Error while walking through file system on path '" + root + "': " + ex.getMessage(), ex);
        }
    }
}
