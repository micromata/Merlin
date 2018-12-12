package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.BufferRecyclers;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class I18nJsonConverter {
    private static Logger log = LoggerFactory.getLogger(I18nJsonConverter.class);

    @Getter
    private Dictionary translations;
    @Setter
    private String carriageReturn = "\n";
    @Setter
    private boolean keysOnly;

    /**
     * If false (default) all translations will be written. If true, only "" will be written for every language.
     */
    @Setter
    private boolean writeEmptyTranslations = false;

    public I18nJsonConverter() {
        this.translations = new Dictionary();
    }

    public I18nJsonConverter(Dictionary translations) {
        this.translations = translations;
    }

    public void importTranslations(Reader reader) throws IOException {
        Map<String, I18nJsonEntry> map = new HashMap<>();
        StringWriter writer = new StringWriter();
        IOUtils.copy(reader, writer);
        ObjectMapper mapper = new ObjectMapper();
        map = mapper.readValue(writer.toString(), new TypeReference<Map<String, I18nJsonEntry>>() {
        });
        for (Map.Entry<String, I18nJsonEntry> mapEntry : map.entrySet()) {
            String key = mapEntry.getKey();
            I18nJsonEntry entry = mapEntry.getValue();
            for (Map.Entry<String, String> translation : entry.value.entrySet()) {
                translations.addTranslation(translation.getKey(), key, translation.getValue());
            }
        }
    }

    public void write(Writer writer) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean firstKey = true;
        for (String key : translations.getKeys()) {
            if (firstKey) firstKey = false;
            else sb.append(",");
            sb.append(carriageReturn);
            sb.append("  \"").append(key).append("\": {").append(carriageReturn); // "de.micromata.key": {
            sb.append("    \"value\": {").append(carriageReturn);                 //   "value" : {
            boolean firstLang = true;
            for (String lang : translations.getUsedLangs()) {
                String text = keysOnly ? "" : escapeJson(translations.getTranslation(lang, key));
                if (firstLang) firstLang = false;
                else sb.append(",").append(carriageReturn);
                sb.append("      \"").append(lang).append("\": \"")
                        .append(text).append("\"");                               //     "de": "Schlüssel"
            }
            sb.append(carriageReturn).append("    },").append(carriageReturn);    //   },
            sb.append("    \"default\": \"").append(key).append("\"")
                    .append(carriageReturn);                                      //   "default": "de.micromata.key"
            sb.append("  }");                                                     // }
        }
        sb.append(carriageReturn).append("}").append(carriageReturn);
        writer.write(sb.toString());
        writer.flush();
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return new String(BufferRecyclers.getJsonStringEncoder().quoteAsString(text));
    }
}
