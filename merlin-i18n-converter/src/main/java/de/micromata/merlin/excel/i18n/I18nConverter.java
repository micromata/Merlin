package de.micromata.merlin.excel.i18n;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class I18nConverter {
    private static Logger log = LoggerFactory.getLogger(I18nConverter.class);

    @Getter
    private Translations translations;

    public I18nConverter() {
        this.translations = new Translations();
    }

    public I18nConverter(Translations translations) {
        this.translations = translations;
    }

    public void importTranslations(File file) throws IOException {
        if (!file.exists()) {
            log.error("File '" + file.getAbsolutePath() + "' doesn't exist. Skipping.");
            return;
        }
        translations = new Translations();
        translations.setCreateKeyIfNotPresent(true);
        translations.setOverwriteExistingTranslations(false);
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if (extension.startsWith("xls")) {
            I18nExcelConverter i18nExcelConverter = new I18nExcelConverter(translations);
            try (InputStream intputStream = new FileInputStream(file)) {
                i18nExcelConverter.importTranslations(intputStream, file.getName());
            }
            return;
        }
        if (extension.startsWith("js")) {
            I18nJsonConverter jsonConverter = new I18nJsonConverter(translations);
            try (Reader reader = new FileReader(file)) {
                jsonConverter.importTranslations(reader);
            }
            return;
        }
        I18nPropertiesConverter i18nPropertiesConverter = new I18nPropertiesConverter(translations);
        String basename = FilenameUtils.getBaseName(file.getName());
        int pos = basename.indexOf('_');
        String lang = pos >= 0 && pos < basename.length() -1 ? basename.substring(pos + 1) : "";
        try (Reader reader = new FileReader(file)) {
            i18nPropertiesConverter.importTranslations(lang, reader);
        }
    }
}
