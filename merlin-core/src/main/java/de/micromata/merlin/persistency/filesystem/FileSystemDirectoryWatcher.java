package de.micromata.merlin.persistency.filesystem;

import de.micromata.merlin.persistency.AbstractDirectoryWatcher;
import de.micromata.merlin.persistency.DirectoryWatcherContext;
import de.micromata.merlin.persistency.ItemType;
import de.micromata.merlin.persistency.PersistencyRegistry;
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

    public FileSystemDirectoryWatcher(Path root, boolean recursive, String... fileExtensions) {
        super(PersistencyRegistry.getDefault().getCanonicalPath(root), recursive, fileExtensions);
    }

    @Override
    protected synchronized void walkTree(DirectoryWatcherContext context) {
        if (!Files.exists(rootDir)) {
            log.warn("Directory '" + rootDir.toAbsolutePath() + "' doesn't exist. Ignoring it.");
            return;
        }
        try {
            log.debug("Walking through the file system: " + rootDir);
            Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    long lastModified = attrs.lastModifiedTime().toMillis();
                    visit(dir, ItemType.DIR, lastModified, context);
                    if (isRecursive() || dir.equals(rootDir)) {
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    long lastModified = attrs.lastModifiedTime().toMillis();
                    visit(file, ItemType.FILE, lastModified, context);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            log.error("Error while walking through file system on path '" + rootDir + "': " + ex.getMessage(), ex);
        }
    }
}
