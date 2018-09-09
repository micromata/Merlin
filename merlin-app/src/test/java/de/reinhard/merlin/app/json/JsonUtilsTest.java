package de.reinhard.merlin.app.json;

import de.reinhard.merlin.word.templating.TemplateDef;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonUtilsTest {
    private Logger log = LoggerFactory.getLogger(JsonUtilsTest.class);

    @Test
    public void toJsonTest() throws IOException {
        TemplateDef def = new TemplateDef();
        def.setFilenamePattern("Contract");
        log.info(JsonUtils.toJson(def));
        assertEquals("{\"filenamePattern\":\"Contract\"}", JsonUtils.toJson(def));
    }
}