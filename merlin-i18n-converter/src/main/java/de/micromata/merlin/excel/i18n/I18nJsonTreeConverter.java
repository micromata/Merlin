package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

public class I18nJsonTreeConverter {
    private static Logger log = LoggerFactory.getLogger(I18nJsonTreeConverter.class);

    @Getter
    private Dictionary dictionary;
    @Setter
    private String carriageReturn = "\n";
    @Setter
    private boolean keysOnly;

    /**
     * If false (default) all dictionary will be written. If true, only "" will be written for every language.
     */
    @Setter
    private boolean writeEmptyTranslations = false;

    public I18nJsonTreeConverter() {
        this.dictionary = new Dictionary();
    }

    public I18nJsonTreeConverter(Dictionary translations) {
        this.dictionary = translations;
    }

    public void importTranslations(Reader reader, String lang) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(reader);
        traverse(lang, rootNode, null);
    }

    private void traverse(String lang, JsonNode node, String parentKey) {
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> childEntry = it.next();
            String key = childEntry.getKey();
            JsonNode child = childEntry.getValue();
            if (child.getNodeType() == JsonNodeType.OBJECT) {
                traverse(lang, child, buildKey(parentKey, key));
            } else {
                dictionary.addTranslation(lang, buildKey(parentKey, key), child.textValue());
            }
        }
    }

    private String buildKey(String parentKey, String key) {
        if (StringUtils.isNotBlank(parentKey)) {
            return parentKey + "." + key;
        }
        return key;
    }


    public void write(Writer writer) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean firstKey = true;
        for (String key : dictionary.getKeys()) {
            if (firstKey) firstKey = false;
            else sb.append(",");
            sb.append(carriageReturn);
            sb.append("  \"").append(key).append("\": {").append(carriageReturn); // "de.micromata.key": {
            sb.append("    \"value\": {").append(carriageReturn);                 //   "value" : {
            boolean firstLang = true;
            for (String lang : dictionary.getUsedLangs()) {
                String text = keysOnly ? "" : escapeJson(dictionary.getTranslation(lang, key));
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
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return StringEscapeUtils.escapeJson(text);
    }
}
