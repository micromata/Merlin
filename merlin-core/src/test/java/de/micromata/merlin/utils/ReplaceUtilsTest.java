package de.micromata.merlin.utils;

import de.micromata.merlin.word.templating.Variables;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReplaceUtilsTest {
    @Test
    public void replaceTest() {
        Variables variables = new Variables();
        variables.put("a", "A");
        variables.put("b", "Berta");
        variables.put("name", "hurz");
        assertEquals("", ReplaceUtils.replace("", variables));
        assertEquals("A", ReplaceUtils.replace("${a}", variables));
        assertEquals("Berta", ReplaceUtils.replace("${b}", variables));
        assertEquals("hurz, A, Berta", ReplaceUtils.replace("${name},{* comment } ${a}, ${b}", variables));
    }

    @Test
    public void encodeFilenameTest() {
        assertEquals("file", ReplaceUtils.encodeFilename(null, true));
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-",
                ReplaceUtils.encodeFilename("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-", true));
        assertEquals("_", ReplaceUtils.encodeFilename(" ", true));
        assertEquals("__", ReplaceUtils.encodeFilename("  ", true));
        assertEquals("Kai_Oester__Test", ReplaceUtils.encodeFilename("Kai Öster:,Test", true));
        assertEquals("AeOeUeaeoeuess", ReplaceUtils.encodeFilename("ÄÖÜäöüß", true));
        assertEquals("Stephanie", ReplaceUtils.encodeFilename("Stéphanie", true));
        assertEquals("AGOOacae", ReplaceUtils.encodeFilename("ĂĠÒǬåçä", true));

        assertEquals("Ä____.___.__", ReplaceUtils.encodeFilename("Ä\"*/:.<>?.\\|", false));
        StringBuilder sb = new StringBuilder();
        for (char ch = 0; ch <= 31; ch++) {
            sb.append(ch);
        }
        sb.append("xxx").append((char)127);
        assertEquals("________________________________xxx_", ReplaceUtils.encodeFilename(sb.toString(), false));
        assertEquals("Ä é", ReplaceUtils.encodeFilename("Ä é", false));
        sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("1234567890");
        }
        assertEquals(255, ReplaceUtils.encodeFilename(sb.toString(), false).length());
    }

    @Test
    public void replaceGermanUmlauteAndAccents() {
        assertNull(ReplaceUtils.replaceGermanUmlauteAndAccents(null));
        assertEquals("", ReplaceUtils.replaceGermanUmlauteAndAccents(""));
        assertEquals("AGOOacAeaeOeoeUeuessnormal_ .", ReplaceUtils.replaceGermanUmlauteAndAccents("ĂĠÒǬåçÄäÖöÜüßnormal_ ."));
    }
}
