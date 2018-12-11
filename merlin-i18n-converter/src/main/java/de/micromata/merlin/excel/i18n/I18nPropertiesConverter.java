package de.micromata.merlin.excel.i18n;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class I18nPropertiesConverter {
    @Getter
    private Dictionary translations;
    @Setter
    private String carriageReturn = "\n";

    public I18nPropertiesConverter() {
        this.translations = new Dictionary();
    }

    public I18nPropertiesConverter(Dictionary translations) {
        this.translations = translations;
    }

    public void importTranslations(String lang, Reader reader) throws IOException {
        Properties props = new Properties();
        props.load(reader);
        for (Object keyObject : props.keySet()) {
            String key = (String)keyObject;
            String value = (String)props.get(keyObject);
            translations.addTranslation(lang, key, value);
        }
    }

    public void write(String lang, Writer writer) throws IOException {
        for (String key : translations.getKeys()) {
            String text = translations.getTranslation(lang, key);
            if (StringUtils.isEmpty(text)) {
                continue;
            }
            writer.write(key);
            writer.write("=");
            writer.write(text);
            writer.write(carriageReturn);
        }
    }
}
