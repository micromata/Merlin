package de.reinhard.merlin.app.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    private static Logger log = LoggerFactory.getLogger(JsonUtils.class);

    public static String toJson(Object obj) {
        StringWriter writer = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(writer, obj);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
        return writer.toString();
    }

    public static <T> T fromJson(Class<T> clazz, String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static <T> List<T> fromJsonToArray(String jsonArray) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonArray, new TypeReference<List<T>>() {
            });
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static <T> Map<String, T> fromJsonToMap(String jsonMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonMap, new TypeReference<Map<String, T>>() {
            });
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }
}
