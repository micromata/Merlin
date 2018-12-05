package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class I18nJsonImporter {
    private static Logger log = LoggerFactory.getLogger(I18nJsonImporter.class);

    @Getter
    private Translations translations;
    @Setter
    private String carriageReturn = "\n";

    public I18nJsonImporter() {
        this.translations = new Translations();
    }

    public I18nJsonImporter(Translations translations) {
        this.translations = translations;
    }

    public void importTranslations(Reader reader) throws IOException {
        Map<String, I18nJsonImportEntry> map = new HashMap<>();
        StringWriter writer = new StringWriter();
        IOUtils.copy(reader, writer);
        ObjectMapper mapper = new ObjectMapper();
        map = mapper.readValue(writer.toString(), new TypeReference<Map<String, I18nJsonImportEntry>>() {
        });
        for (Map.Entry<String, I18nJsonImportEntry> mapEntry : map.entrySet()) {
            String key = mapEntry.getKey();
            I18nJsonImportEntry entry = mapEntry.getValue();
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
            sb.append("\"").append(key).append("\": {").append(carriageReturn); // "de.micromata.key": {
            sb.append("  \"value\": {").append(carriageReturn);                 //   "value" : {
            boolean firstLang = true;
            for (String lang : translations.getUsedLangs()) {
                String text = StringUtils.defaultString(translations.getTranslation(lang, key));
                if (firstLang) firstLang = false;
                else sb.append(",").append(carriageReturn);
                sb.append("    \"").append(key).append("\": \"")
                        .append(text).append("\"");                             //     "de": "Schlüssel"
                writer.write("=");
                writer.write(text);
                writer.write("\n");
            }
            sb.append("  },").append(carriageReturn);                           //   },
            sb.append("  \"default\": \"").append(key).append("\"")
                    .append(carriageReturn);                                    //   "default": "de.micromata.key"
            sb.append("}");                                                     // }
        }
        sb.append("}").append(carriageReturn);
        writer.write(sb.toString());
    }

    public class ImportTranslation {
        private Map<String, String> value;
        private String defaultValue;
    }
}
