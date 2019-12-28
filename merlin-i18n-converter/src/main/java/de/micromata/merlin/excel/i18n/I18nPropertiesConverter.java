package de.micromata.merlin.excel.i18n;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class I18nPropertiesConverter {
    private Dictionary dictionary;
    private String carriageReturn = "\n";

    public I18nPropertiesConverter() {
        this.dictionary = new Dictionary();
    }

    public I18nPropertiesConverter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void importTranslations(String lang, Reader reader) throws IOException {
        Properties props = new Properties();
        props.load(reader);
        for (Object keyObject : props.keySet()) {
            String key = (String)keyObject;
            String value = (String)props.get(keyObject);
            dictionary.addTranslation(lang, key, value);
        }
    }

    public void write(String lang, Writer writer) throws IOException {
        for (String key : dictionary.getKeys()) {
            String text = dictionary.getTranslation(lang, key);
            if (StringUtils.isEmpty(text)) {
                continue;
            }
            writer.write(key);
            writer.write("=");
            writer.write(text);
            writer.write(carriageReturn);
        }
        writer.flush();
    }

    public Dictionary getDictionary() {
        return this.dictionary;
    }

    public void setCarriageReturn(String carriageReturn) {
        this.carriageReturn = carriageReturn;
    }
}
