package de.reinhard.merlin.app.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.reinhard.merlin.app.ConfigurationTemplateDir;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * A fix before the web-app supports multi template dirs.
 */
public class ConfigurationTestSerializer extends StdSerializer<List<ConfigurationTemplateDir>> {
    public ConfigurationTestSerializer() {
        this(null);
    }

    public ConfigurationTestSerializer(Class<List<ConfigurationTemplateDir>> t) {
        super(t);
    }

    @Override
    public void serialize(
            List<ConfigurationTemplateDir> value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {
        if (CollectionUtils.isEmpty(value)) {
            gen.writeNull();
            return;
        }
        gen.writeString(value.get(0).getDirectory());
    }
}
