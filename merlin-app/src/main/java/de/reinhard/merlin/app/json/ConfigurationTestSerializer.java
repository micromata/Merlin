package de.reinhard.merlin.app.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * A fix before the web-app supports multi template dirs.
 */
public class ConfigurationTestSerializer extends StdSerializer<List<String>> {
    public ConfigurationTestSerializer() {
        this(null);
    }

    public ConfigurationTestSerializer(Class<List<String>> t) {
        super(t);
    }

    @Override
    public void serialize(
            List<String> value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {
        if (CollectionUtils.isEmpty(value)) {
            gen.writeNull();
            return;
        }
        gen.writeString(value.get(0));
    }
}
