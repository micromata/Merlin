package de.reinhard.merlin.persistency.filesystem;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.persistency.DirectoryWatchEntry;
import de.reinhard.merlin.persistency.ItemType;
import de.reinhard.merlin.persistency.ModificationType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FilesystemDirectoryWatcherTest {
    @Test
    public void replaceTest() throws IOException {
        File rootDir = new File(Definitions.OUTPUT_DIR, "directoryWatcherTest").getAbsoluteFile();
        if (rootDir.exists()) {
            FileUtils.deleteDirectory(rootDir);
        }
        rootDir.mkdir();
        FileSystemDirectoryWatcher watcher = new FileSystemDirectoryWatcher(rootDir.toPath(), "xls", "xlsx", "docx");
        File f1 = writeFile(rootDir, "test.xlsx");
        watcher.walkTree();
        assertEntry(watcher, f1, ItemType.FILE, null);

        touch(f1);
        File f2 = writeFile(rootDir, "ignore.java");
        File d_a = mkdir(rootDir, "a");
        File d_a_1 = mkdir(d_a, "a1");
        File d_a_2 = mkdir(d_a, "a2");
        File f_a_2 = writeFile(d_a_2, "test_a2.xlsx");
        watcher.walkTree();
        assertNull(watcher.getFileEntry(f2.toPath()), "files *.java should be ignored.");
        assertEntry(watcher, f1, ItemType.FILE, null);
        assertEntry(watcher, d_a, ItemType.DIR, ModificationType.CREATED);
        assertEntry(watcher, f_a_2, ItemType.FILE, ModificationType.CREATED);

        FileUtils.deleteDirectory(d_a);
        watcher.walkTree();
        assertNull(watcher.getFileEntry(f_a_2.toPath()));
        assertEntry(watcher, f1, ItemType.FILE, null);

        f1.delete();
        watcher.walkTree();
        assertNull(watcher.getFileEntry(f1.toPath()));
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

    private void assertEntry(FileSystemDirectoryWatcher watcher, File path, ItemType itemType, ModificationType modificationType) {
        DirectoryWatchEntry entry = watcher.getEntry(path.toPath(), itemType);
        if (modificationType == null) {
            assertNull(entry.getType());
        } else {
            assertEquals(modificationType, entry.getType());
        }
        assertEquals(path.lastModified(), entry.getLastModified());
    }
}
