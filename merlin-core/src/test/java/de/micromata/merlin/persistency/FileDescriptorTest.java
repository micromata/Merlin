package de.micromata.merlin.persistency;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileDescriptorTest {
    private Logger log = LoggerFactory.getLogger(FileDescriptorTest.class);

    private static final String TEST_DIR = "./target/";

    @Test
    public void matchesTest() {
        FileDescriptor descriptor1 = new FileDescriptor();
        FileDescriptor descriptor2 = new FileDescriptor();
        assertFalse(descriptor1.matches(descriptor2));
        descriptor1.setDirectory("/Users/kai");
        descriptor2.setDirectory("/Users/kai");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor1.setDirectory("Documents/templates");
        descriptor2.setDirectory("Documents/templates");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor1.setFilename("ContractTemplate.docx");
        descriptor2.setFilename("ContractTemplate.xlsx");
        assertTrue(descriptor1.matches(descriptor2));

        descriptor2.setDirectory("/Users/horst");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor2.setDirectory("/Users/kai");
        descriptor2.setDirectory("Documents/templates/test");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor2.setDirectory("Documents/templates");
        descriptor2.setFilename("ContractTemplate2.xlsx");
        assertFalse(descriptor1.matches(descriptor2));
        descriptor2.setFilename("ContractTemplate.xls");
        assertTrue(descriptor1.matches(descriptor2));
    }

    @Test
    public void lastModifiedTest() throws IOException, InterruptedException {
        File file = new File(TEST_DIR, "tmp.txt");
        FileUtils.write(file, "Test", Charset.defaultCharset());
        Date now = new Date();
        FileDescriptor descriptor = new FileDescriptor();
        assertTrue(descriptor.isModified(file.toPath())); // last update not set.
        descriptor.setLastUpdate(new Date(now.getTime() + 1000));
        assertFalse(descriptor.isModified(file.toPath())); // last update is 1s in the future.
        descriptor.setLastUpdate(new Date(now.getTime() - 1000));
        FileUtils.write(file, "Test", Charset.defaultCharset());
        assertTrue(descriptor.isModified(file.toPath())); // last update is 1s in the past.
    }

    @Test
    public void relativizePathTest() {
        File dir = new File("/Users/kai/Documents");
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setDirectory(dir.toPath());
        File file = new File("/Users/kai/Documents/templates/template.xls");
        fileDescriptor.setRelativePath(file.toPath());
        assertEquals("templates", fileDescriptor.getRelativePath());
        file = new File("/Users/kai/template.xls");
        fileDescriptor.setRelativePath(file.toPath());
        assertEquals("..", fileDescriptor.getRelativePath());
        file = new File("/Users/kai/templates/template.xls");
        fileDescriptor.setRelativePath(file.toPath());
        assertEquals(".." + File.separator + "templates", fileDescriptor.getRelativePath());
        file = new File("/Users/kai/Documents/template.xls");
        fileDescriptor.setRelativePath(file.toPath());
        assertEquals("", fileDescriptor.getRelativePath());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void canonicalPathTest() {
        File dir = new File("/Users/kai/Documents");
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setDirectory(dir.toPath());
        File file = new File("/Users/kai/Documents/templates/template.xls");
        fileDescriptor.setRelativePath(file.toPath());
        // on Windows: "R:\Users\kai\Documents\templates\template.xls"
        assertEquals("/Users/kai/Documents/templates/template.xls", fileDescriptor.getCanonicalPathString());
        file = new File("/Users/kai/Documents/template.xls");
        fileDescriptor.setRelativePath(file.toPath());
        // on Windows: "R:\Users\kai\Documents\template.xls"
        assertEquals("/Users/kai/Documents/template.xls", fileDescriptor.getCanonicalPathString());
        file = new File("/Users/kai/tmp/template.xls");
        fileDescriptor.setRelativePath(file.toPath());
        // on Windows: "R:\Users\kai\tmp\template.xls"
        assertEquals("/Users/kai/tmp/template.xls", fileDescriptor.getCanonicalPathString());
    }
}
