package de.micromata.merlin.excel.i18n;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class I18nConverter {
    private static Logger log = LoggerFactory.getLogger(I18nConverter.class);

    @Getter
    private Dictionary dictionary;

    // Key is source file, value is dest file.
    @Getter
    private Map<File, File> importedFiles = new HashMap<>();

    public I18nConverter() {
        this.dictionary = new Dictionary();
    }

    public I18nConverter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void importTranslations(File file) throws IOException {
        if (!file.exists()) {
            log.error("File '" + file.getAbsolutePath() + "' doesn't exist. Skipping.");
            return;
        }
        if (file.isDirectory()) {
            log.info("Reading json tree from directory: " + file.getAbsolutePath());
            Path baseDirPath = file.toPath();
            for (File subdir : file.listFiles()) {
                if (subdir.getName().length() == 2) {
                    String lang = subdir.getName();
                    log.info("Processing lang '" + lang + "'.");
                    for (File translationFile : subdir.listFiles()) {
                        if (translationFile.getName().endsWith(".json")) {
                            log.info("Processing json tree for language '" + lang + "' with file: " + translationFile.getAbsolutePath());
                            importJsonTree(lang, translationFile);
                            Path filePath = translationFile.toPath();
                            importedFiles.put(translationFile, baseDirPath.relativize(filePath).toFile());
                        }
                    }
                }
            }
            return;
        }
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if (extension.startsWith("xls")) {
            importExcel(file);
            importedFiles.put(file, new File(file.getName()));
            return;
        }
        String content = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
        if (content.trim().startsWith("{")) {
            importJson(content, file);
            importedFiles.put(file, new File(file.getName()));
            return;
        }
        importProperties(content, file);
        importedFiles.put(file, new File(file.getName()));
    }

    private void importExcel(File file) throws IOException {
        log.info("Importing Excel translations: " + file.getAbsolutePath());
        I18nExcelConverter i18nExcelConverter = new I18nExcelConverter(dictionary);
        try (InputStream intputStream = new FileInputStream(file)) {
            i18nExcelConverter.importTranslations(intputStream, file.getName());
        }
    }

    private void importJson(String content, File file) throws IOException {
        log.info("Importing json translations: " + file.getAbsolutePath());
        I18nJsonConverter jsonConverter = new I18nJsonConverter(dictionary);
        try (Reader reader = new StringReader(content)) {
            jsonConverter.importTranslations(reader);
        }
    }

    private void importJsonTree(String lang, File file) throws IOException {
        log.info("Importing json translations (lang=" + lang + "): " + file.getAbsolutePath());
        I18nJsonTreeConverter jsonConverter = new I18nJsonTreeConverter(dictionary);
        try (Reader reader = new FileReader(file)) {
            jsonConverter.importTranslations(reader, lang);
        }
    }

    private void importProperties(String content, File file) throws IOException {
        I18nPropertiesConverter i18nPropertiesConverter = new I18nPropertiesConverter(dictionary);
        String basename = FilenameUtils.getBaseName(file.getName());
        int pos = basename.indexOf('_');
        String lang = pos >= 0 && pos < basename.length() - 1 ? basename.substring(pos + 1) : "";
        log.info("Importing properties translations for lang '" + lang + "': " + file.getAbsolutePath());
        try (Reader reader = new StringReader(content)) {
            i18nPropertiesConverter.importTranslations(lang, reader);
        }
    }
}
