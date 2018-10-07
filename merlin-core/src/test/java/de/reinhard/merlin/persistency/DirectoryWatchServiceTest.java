package de.reinhard.merlin.persistency;

import de.reinhard.merlin.Definitions;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class DirectoryWatchServiceTest {
    private Logger log = LoggerFactory.getLogger(DirectoryWatchServiceTest.class);

    @Test
    public void watchRecursiveTest() throws Exception {
        File rootDir = new File(Definitions.OUTPUT_DIR, "directoryWatchServiceTest");
        if (rootDir.exists()) {
            FileUtils.deleteDirectory(rootDir);
        }
        rootDir.mkdir();
        DirectoryWatchService ws = new DirectoryWatchService();
        ws.register(rootDir.toPath(), true);
        ws.start();

        File f1 = writeFile(rootDir, "test.xlsx");
        File d_a = new File(rootDir, "a");
        d_a.mkdir();
        File d_a_1 = new File(d_a, "a1");
        d_a_1.mkdir();
        File d_a_2 = new File(d_a, "a2");
        d_a_2.mkdir();
        Thread.sleep(10000);
        File f_a_1 = writeFile(d_a_2, "test_a2.xlsx");
        Thread.sleep(50000);
    }

    private File writeFile(File dir, String filename) throws IOException {
        File file = new File(dir, filename);
        FileUtils.write(file, "Merlin is great.", Charset.defaultCharset());
        return file;
    }
}
