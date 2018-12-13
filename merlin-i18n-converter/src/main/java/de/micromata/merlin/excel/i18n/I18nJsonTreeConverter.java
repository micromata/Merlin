package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.core.util.BufferRecyclers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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

    public I18nJsonTreeConverter(Dictionary dictionary) {
        this.dictionary = dictionary;
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
            } else if (child.getNodeType() == JsonNodeType.ARRAY) {
                dictionary.addTranslation(lang, buildKey(parentKey, key), prettyPrintJsonString(child));
            } else {
                dictionary.addTranslation(lang, buildKey(parentKey, key), child.textValue());
            }
        }
    }

    public String prettyPrintJsonString(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception ex) {
            log.error("*** error while converting json: " + ex.getMessage());
            return "*** error while converting json: " + ex.getMessage();
        }
    }

    private String buildKey(String parentKey, String key) {
        if (StringUtils.isNotBlank(parentKey)) {
            return parentKey + "." + key;
        }
        return key;
    }


    public void write(String lang, Writer writer) throws IOException {
        Node root = buildNodes();
        StringBuilder sb = new StringBuilder();
        write(lang, sb, root);
        writer.write(sb.toString());
        writer.flush();
    }

    private void write(String lang, StringBuilder sb, Node node) {
        if (node.level > 0) {
            // Skip this only for root note:
            for (int i = 0; i < node.level; i++) sb.append("  ");
            sb.append("\"").append(node.keyPart).append("\": ");
        }
        if (node.childs == null) {
            String translation = dictionary.getTranslation(lang, node.i18nKey);
            if (isJsonContent(translation)) {
                for (String line : translation.split("\n")) {
                    for (int i = 0; i < node.level; i++) sb.append("  ");
                    sb.append(line).append("\n");
                }
            } else {
                sb.append("\"").append(escapeJson(translation)).append("\"");
            }
            return;
        }
        sb.append("{");
        int counter = node.childs.size();
        for (Map.Entry<String, Node> entry : node.childs.entrySet()) {
            sb.append(carriageReturn);
            write(lang, sb, entry.getValue());
            if (--counter > 0) {
                sb.append(",");
            }
        }
        sb.append(carriageReturn);
        for (int i = 0; i < node.level; i++) sb.append("  ");
        sb.append("}");
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        if (isJsonContent(text)) {
            return text;
        }
        return new String(BufferRecyclers.getJsonStringEncoder().quoteAsString(text));
    }

    private boolean isJsonContent(String text) {
        return text != null && StringUtils.deleteWhitespace(text).startsWith("[{");
    }

    private Node buildNodes() {
        Node root = new Node(null, 0);
        for (String i18nKey : dictionary.getKeys()) {
            String[] keyParts = StringUtils.split(i18nKey, '.');
            addNodes(root, i18nKey, keyParts, 0);
        }
        return root;
    }

    private void addNodes(Node parent, String i18nKey, String[] keyParts, int level) {
        String keyPart = keyParts[level];
        Node node = parent.ensureAndGetChild(keyPart);
        if (level < keyParts.length - 1) {
            addNodes(node, i18nKey, keyParts, level + 1);
        } else {
            node.i18nKey = i18nKey;
        }
    }

    private class Node implements Comparable<Node> {
        String keyPart;
        String i18nKey;
        int level;
        Map<String, Node> childs;

        Node(String keyPart, int level) {
            this.keyPart = keyPart;
            this.level = level;
        }

        Node ensureAndGetChild(String keyPart) {
            if (childs == null) {
                childs = new TreeMap<String, Node>();
            }
            Node child = childs.get(keyPart);
            if (child == null) {
                child = new Node(keyPart, level + 1);
                childs.put(keyPart, child);
            }
            return child;
        }

        @Override
        public int compareTo(Node o) {
            return keyPart.compareTo(o.keyPart);
        }
    }
}
