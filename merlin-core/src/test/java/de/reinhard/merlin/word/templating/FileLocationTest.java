package de.reinhard.merlin.word.templating;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileLocationTest {

    @Test
    public void matchesTest() {
        FileLocation location1 = new FileLocation();
        FileLocation location2 = new FileLocation();
        assertFalse(location1.matches(location2));
        location1.setDirectory("/Users/kai");
        location2.setDirectory("/Users/kai");
        assertFalse(location1.matches(location2));
        location1.setDirectory("Documents/templates");
        location2.setDirectory("Documents/templates");
        assertFalse(location1.matches(location2));
        location1.setFilename("ContractTemplate.docx");
        location2.setFilename("ContractTemplate.xlsx");
        assertTrue(location1.matches(location2));

        location2.setDirectory("/Users/horst");
        assertFalse(location1.matches(location2));
        location2.setDirectory("/Users/kai");
        location2.setDirectory("Documents/templates/test");
        assertFalse(location1.matches(location2));
        location2.setDirectory("Documents/templates");
        location2.setFilename("ContractTemplate2.xlsx");
        assertFalse(location1.matches(location2));
        location2.setFilename("ContractTemplate.xls");
        assertTrue(location1.matches(location2));
    }
}
