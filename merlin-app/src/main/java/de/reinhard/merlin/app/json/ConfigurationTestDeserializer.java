package de.reinhard.merlin.app.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.reinhard.merlin.app.ConfigurationTemplateDir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fix before the web-app supports multi template dirs.
 */
public class ConfigurationTestDeserializer extends StdDeserializer<List<ConfigurationTemplateDir>> {
    public ConfigurationTestDeserializer() {
        this(null);
    }

    public ConfigurationTestDeserializer(Class<List<ConfigurationTemplateDir>> t) {
        super(t);
    }

    @Override
    public List<ConfigurationTemplateDir> deserialize(
            JsonParser jsonparser, DeserializationContext context)
            throws IOException {

        String templateDirs = jsonparser.getText();
        List<ConfigurationTemplateDir> list = new ArrayList<>();
        list.add(new ConfigurationTemplateDir().setDirectory(templateDirs));
        return list;
    }
}
