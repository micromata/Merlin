package de.reinhard.merlin.app.json;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

public class JsonUtils {
    private static Logger log = LoggerFactory.getLogger(JsonUtils.class);

    public static String toJson(Object obj) {
        ObjectMapper objectMapper = getObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, obj);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
        return writer.toString();
    }

    public static <T> T fromJson(Class<T> clazz, String json) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        Class<?>[] definitions = JsonIdGenerator.class.getDeclaredClasses();
        for (Class<?> definition : definitions) {
            objectMapper.addMixIn(definition.getAnnotation(JsonIdentityInfo.class).scope(), definition);
        }
        return objectMapper;
    }
}
