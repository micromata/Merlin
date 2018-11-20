package de.micromata.paypal.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

public class JsonUtils {
    private static Logger log = LoggerFactory.getLogger(JsonUtils.class);


    public static String toJson(Object obj) {
        return toJson(obj, false);
    }

    /**
     * @param obj
     * @param prettyPrinter If true, the json output will be pretty printed (human readable with new lines and indenting).
     * @return
     */
    public static String toJson(Object obj, boolean prettyPrinter) {
        if (obj == null) {
            return "";
        }
        ObjectMapper objectMapper = getObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            if (prettyPrinter) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                StringWriter writer = new StringWriter();
                objectMapper.writeValue(writer, obj);
                return writer.toString();
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
    }

    public static <T> T fromJson(Class<T> clazz, String json) {
        ObjectMapper objectMapper = getObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static <T> T fromJson(final TypeReference<T> type, final String json) {
        try {
            T data = new ObjectMapper().readValue(json, type);
            return data;
        } catch (Exception ex) {
            log.error("Json: '" + json + "': " + ex.getMessage(), ex);
        }
        return null;
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
        //SimpleModule module = new SimpleModule();
        //module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        //objectMapper.registerModule(module);
        //Class<?>[] definitions = JsonIdGenerator.class.getDeclaredClasses();
        //for (Class<?> definition : definitions) {
        //    objectMapper.addMixIn(definition.getAnnotation(JsonIdentityInfo.class).scope(), definition);
        //}
        return objectMapper;
    }
}
