package de.micromata.merlin.excel.i18n;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

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
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if (extension.startsWith("xls")) {
            importExcel(file);
            return;
        }
        String content = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
        if (content.trim().startsWith("{")) {
            importJson(content, file);
            return;
        }
        importProperties(content, file);
    }

    private void importExcel(File file) throws IOException {
        log.info("Importing Excel translations: " + file.getAbsolutePath());
        I18nExcelConverter i18nExcelConverter = new I18nExcelConverter(translations);
        try (InputStream intputStream = new FileInputStream(file)) {
            i18nExcelConverter.importTranslations(intputStream, file.getName());
        }
    }

    private void importJson(String content, File file) throws IOException {
        log.info("Importing json translations: " + file.getAbsolutePath());
        I18nJsonConverter jsonConverter = new I18nJsonConverter(translations);
        try (Reader reader = new StringReader(content)) {
            jsonConverter.importTranslations(reader);
        }
    }

    private void importProperties(String content, File file) throws IOException {
        I18nPropertiesConverter i18nPropertiesConverter = new I18nPropertiesConverter(translations);
        String basename = FilenameUtils.getBaseName(file.getName());
        int pos = basename.indexOf('_');
        String lang = pos >= 0 && pos < basename.length() - 1 ? basename.substring(pos + 1) : "";
        log.info("Importing properties translations for lang '" + lang + "': " + file.getAbsolutePath());
        try (Reader reader = new StringReader(content)) {
            i18nPropertiesConverter.importTranslations(lang, reader);
        }
    }
}
