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


    public void write(String lang, Writer writer) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Iterator<String> it = dictionary.getKeys().iterator();
        write(lang, sb, it, "", 0);
        writer.write(sb.toString());
        sb.append(carriageReturn).append("}").append(carriageReturn);
    }

    private void write(String lang, StringBuilder sb, Iterator<String> it, String prefix, int level) {
        boolean firstKey = true;
        while (it.hasNext()) {
            String key = it.next();
            String translation = dictionary.getTranslation(key, lang);
            if (firstKey) firstKey = false;
            else sb.append(",");
            sb.append(carriageReturn);
            sb.append(StringUtils.repeat("  ", level));
            sb.append(key).append("\":  \"").append(translation).append("\"");
        }
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return StringEscapeUtils.escapeJson(text);
    }
}
