package de.micromata.merlin.persistency.filesystem;

import de.micromata.merlin.Definitions;
import de.micromata.merlin.persistency.DirectoryWatchEntry;
import de.micromata.merlin.persistency.ModificationType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

class FilesystemDirectoryWatcherTest {
    @Test
    void watcherTest() throws Exception {
        File rootDir = new File(Definitions.OUTPUT_DIR, "directoryWatcherTest").getAbsoluteFile();
        if (rootDir.exists()) {
            FileUtils.deleteDirectory(rootDir);
        }
        rootDir.mkdir();
        FileSystemDirectoryWatcher recursiveWatcher = new FileSystemDirectoryWatcher(rootDir.toPath(), true, "xls", "xlsx", "docx");
        recursiveWatcher.setIgnoreFilenamePatterns("^~\\$.*");
        FileSystemDirectoryWatcher watcher = new FileSystemDirectoryWatcher(rootDir.toPath(), false,"xls", "xlsx", "docx");
        watcher.setIgnoreFilenamePatterns("^~\\$.*");

        File f1 = writeFile(rootDir, "test.xlsx");
        File f2 = writeFile(rootDir, "test2.docx");
        recursiveWatcher.forceRecheck();
        watcher.forceRecheck();
        assertEntry(recursiveWatcher, f1, null);
        assertEntry(recursiveWatcher, f2, null);
        assertEntry(watcher, f1, null);
        assertEntry(watcher, f2, null);
        long lastCheck = recursiveWatcher.getLastCheck();
        assertFalse(recursiveWatcher.isModified(f1.toPath(), lastCheck));
        assertTrue(recursiveWatcher.isModified(f1.toPath(), lastCheck - 1000));

        //Thread.sleep(2000); // For testing new modification time.
        touch(f1);
        File f3 = writeFile(rootDir, "ignore.java");
        File f4 = writeFile(rootDir, "~$ignore.xlsx");
        File d_a = mkdir(rootDir, "a");
        File d_a_1 = mkdir(d_a, "a1");
        File d_a_2 = mkdir(d_a, "a2");
        File f_a_2 = writeFile(d_a_2, "test_a2.xlsx");
        recursiveWatcher.forceRecheck();
        watcher.forceRecheck();
        assertNull(watcher.getEntry(f3.toPath()));
        assertNull(watcher.getEntry(f4.toPath()));
        assertEntry(recursiveWatcher, f1, null);
        assertEntry(watcher, f1, null);
        assertEntry(recursiveWatcher, d_a, ModificationType.CREATED);
        assertEntry(recursiveWatcher, f_a_2, ModificationType.CREATED);
        assertNull(watcher.getEntry(d_a_1.toPath()));
        assertNull(watcher.getEntry(d_a_2.toPath()));
        assertNull(watcher.getEntry(f_a_2.toPath()));

        FileUtils.deleteDirectory(d_a);
        recursiveWatcher.forceRecheck();
        assertEntry(recursiveWatcher, f_a_2, ModificationType.DELETED);
        assertEntry(recursiveWatcher, f1, null);

        f1.delete();
        recursiveWatcher.clear();
        assertNull(recursiveWatcher.getEntry(f1.toPath())); // is not there anymore, because clear of watcher was called.
        assertNull(recursiveWatcher.getEntry(f_a_2.toPath()));
        FileUtils.deleteDirectory(rootDir);
    }

    private File writeFile(File dir, String filename) throws IOException {
        File file = new File(dir, filename);
        FileUtils.write(file, "Merlin is great.", Charset.defaultCharset());
        return file;
    }

    private void touch(File file) throws IOException {
        FileUtils.write(file, "Merlin is great.", Charset.defaultCharset());
    }

    private File mkdir(File dir, String filename) throws IOException {
        File file = new File(dir, filename);
        file.mkdir();
        return file;
    }

    private void assertEntry(FileSystemDirectoryWatcher watcher, File path, ModificationType modificationType) {
        DirectoryWatchEntry entry = watcher.getEntry(path.toPath());
        if (modificationType == null) {
            assertNull(entry.getType());
        } else {
            assertEquals(modificationType, entry.getType());
        }
        if (modificationType != ModificationType.DELETED) {
            assertEquals(path.lastModified(), entry.getLastModified());
        }
    }
}
