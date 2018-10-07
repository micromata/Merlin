package de.reinhard.merlin.persistency.filesystem;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.persistency.DirectoryWatchEntry;
import de.reinhard.merlin.persistency.ModificationType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

public class FilesystemDirectoryWatcherTest {
    @Test
    public void replaceTest() throws Exception {
        File rootDir = new File(Definitions.OUTPUT_DIR, "directoryWatcherTest").getAbsoluteFile();
        if (rootDir.exists()) {
            FileUtils.deleteDirectory(rootDir);
        }
        rootDir.mkdir();
        FileSystemDirectoryWatcher watcher = new FileSystemDirectoryWatcher(rootDir.toPath(), "xls", "xlsx", "docx");
        File f1 = writeFile(rootDir, "test.xlsx");
        File f2 = writeFile(rootDir, "test2.docx");
        watcher.walkTree();
        assertEntry(watcher, f1, null);
        assertEntry(watcher, f2, null);
        long lastCheck = watcher.getLastCheck();
        assertFalse(watcher.isModified(f1.toPath(), lastCheck));
        assertTrue(watcher.isModified(f1.toPath(), lastCheck - 1000));

        //Thread.sleep(2000); // For testing new modification time.
        touch(f1);
        File f3 = writeFile(rootDir, "ignore.java");
        File d_a = mkdir(rootDir, "a");
        File d_a_1 = mkdir(d_a, "a1");
        File d_a_2 = mkdir(d_a, "a2");
        File f_a_2 = writeFile(d_a_2, "test_a2.xlsx");
        watcher.walkTree();
        assertEntry(watcher, f1, null);
        assertEntry(watcher, d_a, ModificationType.CREATED);
        assertEntry(watcher, f_a_2, ModificationType.CREATED);

        FileUtils.deleteDirectory(d_a);
        watcher.walkTree();
        assertEntry(watcher, f_a_2, ModificationType.DELETED);
        assertEntry(watcher, f1, null);
        watcher.clear();

        f1.delete();
        watcher.walkTree();
        assertEntry(watcher, f1, ModificationType.DELETED);
        assertNull(watcher.getEntry(f_a_2.toPath()));
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
