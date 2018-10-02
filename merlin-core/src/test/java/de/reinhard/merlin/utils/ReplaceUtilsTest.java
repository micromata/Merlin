package de.reinhard.merlin.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplaceUtilsTest {
    @Test
    public void replaceTest() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("a", "A");
        variables.put("b", "Berta");
        variables.put("name", "hurz");
        assertEquals("", ReplaceUtils.replace("", variables));
        assertEquals("A", ReplaceUtils.replace("${a}", variables));
        assertEquals("Berta", ReplaceUtils.replace("${b}", variables));
        assertEquals("hurz, A, Berta", ReplaceUtils.replace("${name}, ${a}, ${b}", variables));
    }

    @Test
    public void encodeFilenameTest() {
        assertEquals("file", ReplaceUtils.encodeFilename(null));
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-",
                ReplaceUtils.encodeFilename("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-"));
        assertEquals("_", ReplaceUtils.encodeFilename(" "));
        assertEquals("__", ReplaceUtils.encodeFilename("  "));
        assertEquals("Kai_Oester__Test", ReplaceUtils.encodeFilename("Kai Öster:,Test"));
        assertEquals("AeOeUeaeoeuess", ReplaceUtils.encodeFilename("ÄÖÜäöüß"));
    }
}
