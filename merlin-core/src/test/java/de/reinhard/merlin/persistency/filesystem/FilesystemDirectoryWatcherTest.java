package de.reinhard.merlin.persistency.filesystem;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.utils.ReplaceUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilesystemDirectoryWatcherTest {
    @Test
    public void replaceTest() throws IOException {
        File rootDir = new File(Definitions.OUTPUT_DIR, "directoryWatcherTest");
        if (rootDir.exists()) {
            FileUtils.deleteDirectory(rootDir);
        }
        rootDir.mkdir();
        FileSystemDirectoryWatcher watcher = new FileSystemDirectoryWatcher(rootDir.toPath(), "xls", "xlsx", "docx");
        watcher.walkTree();
        File f1 = writeFile(rootDir, "test.xlsx");
        File f2 = writeFile(rootDir, "ignore.java");
        File d_a = mkdir(rootDir, "a");
        File d_a_1 = mkdir(d_a, "a1");
        File d_a_2 = mkdir(d_a, "a2");
        File f_a_1 = writeFile(d_a_2, "test_a2.xlsx");
        watcher.walkTree();

        assertEquals("", ReplaceUtils.replaceGermanUmlauteAndAccents(""));
        assertEquals("AGOOacAeaeOeoeUeuessnormal_ .", ReplaceUtils.replaceGermanUmlauteAndAccents("ĂĠÒǬåçÄäÖöÜüßnormal_ ."));
    }

    private File writeFile(File dir, String filename) throws IOException {
        File file = new File(dir, filename);
        FileUtils.write(file, "Merlin is great.", Charset.defaultCharset());
        return file;
    }

    private File mkdir(File dir, String filename) throws IOException {
        File file = new File(dir, filename);
        file.mkdir();
        return file;
    }
}
