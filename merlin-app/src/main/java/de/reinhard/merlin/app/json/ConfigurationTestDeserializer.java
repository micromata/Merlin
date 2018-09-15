package de.reinhard.merlin.app.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fix before the web-app supports multi template dirs.
 */
public class ConfigurationTestDeserializer extends StdDeserializer<List<String>> {
    public ConfigurationTestDeserializer() {
        this(null);
    }

    public ConfigurationTestDeserializer(Class<List<String>> t) {
        super(t);
    }

    @Override
    public List<String> deserialize(
            JsonParser jsonparser, DeserializationContext context)
            throws IOException {

        String templateDirs = jsonparser.getText();
        List<String> list = new ArrayList<>();
        list.add(templateDirs);
        return list;
    }
}
