package de.micromata.merlin.excel.i18n;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class I18nConverterTest {
    private final String JSON_FILE = "i18n_messages.json";
    private final String I18N_FILE = "i18n_messages_de.properties";
    private final String TEST_DIR = "test-data";
    private final String OUT_DIR = "out";

    @Test
    void importPropertiesTest() throws IOException {
        I18nPropertiesConverter propsConverter = new I18nPropertiesConverter();
        File file = new File(TEST_DIR, I18N_FILE);
        propsConverter.importTranslations("de", new FileReader(file));
        file = new File(TEST_DIR, "i18n_messages_en.properties");
        propsConverter.importTranslations("en", new FileReader(file));
        Translations translations = propsConverter.getTranslations();
        assertEquals(4, translations.getKeys().size());
        assertEquals("In Blatt ''{0}'', Spalte {1}:''{2}'' und Zeile #{3}: {4}",
                translations.getTranslation("de", "merlin.excel.validation_error.display_all"));
        assertEquals("In sheet ''{0}'', column {1}:''{2}'' and row #{3}: {4}",
                translations.getTranslation("en", "merlin.excel.validation_error.display_all"));
        file = new File(OUT_DIR, I18N_FILE);
        try (Writer writer = new FileWriter(file)) {
            propsConverter.write("de", writer);
        }

        // Reread file:
        propsConverter = new I18nPropertiesConverter();
        propsConverter.importTranslations("de", new FileReader(file));
        translations = propsConverter.getTranslations();
        assertEquals(4, translations.getKeys().size());
        assertEquals("In Blatt ''{0}'', Spalte {1}:''{2}'' und Zeile #{3}: {4}",
                translations.getTranslation("de", "merlin.excel.validation_error.display_all"));
        assertEquals(null, translations.getTranslation("en", "merlin.excel.validation_error.display_all"));
    }

    @Test
    void importJsonTest() throws IOException {
        I18nJsonConverter jsonConverter = new I18nJsonConverter();
        File file = new File(TEST_DIR, JSON_FILE);
        jsonConverter.importTranslations(new FileReader(file));
        Translations translations = jsonConverter.getTranslations();
        assertEquals(3, translations.getKeys().size());
        assertEquals("", translations.getTranslation("de", "merlin.excel.validation_error.display_all"));
        assertEquals("Almeno una lista non è valida. Modifichi la sua scelta altrimenti il voto verrà considerato nullo.",
                translations.getTranslation("it", "merlin.excel.validation_error.error_column_headname"));
        file = new File(OUT_DIR, JSON_FILE);
        try (Writer writer = new FileWriter(file)) {
            jsonConverter.write(writer);
        }

        // Reread file:
        jsonConverter = new I18nJsonConverter();
        jsonConverter.importTranslations(new FileReader(file));
        translations = jsonConverter.getTranslations();
        assertEquals(3, translations.getKeys().size());
        assertEquals("", translations.getTranslation("de", "merlin.excel.validation_error.display_all"));
        assertEquals("Almeno una lista non è valida. Modifichi la sua scelta altrimenti il voto verrà considerato nullo.",
                translations.getTranslation("it", "merlin.excel.validation_error.error_column_headname"));
    }

    @Test
    void multipleImportTest() throws IOException {
        I18nPropertiesConverter propsConverter = new I18nPropertiesConverter();
        File file = new File(TEST_DIR, I18N_FILE);
        propsConverter.importTranslations("de", new FileReader(file));
        file = new File(TEST_DIR, "i18n_messages_en.properties");
        propsConverter.importTranslations("en", new FileReader(file));
        Translations translations = propsConverter.getTranslations();

        I18nJsonConverter jsonConverter = new I18nJsonConverter(translations);
        file = new File(TEST_DIR, JSON_FILE);
        jsonConverter.importTranslations(new FileReader(file));

        assertEquals(4, translations.getKeys().size());
        assertEquals("In Blatt ''{0}'', Spalte {1}:''{2}'' und Zeile #{3}: {4}",
                translations.getTranslation("de", "merlin.excel.validation_error.display_all"));
        assertEquals("Almeno una lista non è valida. Modifichi la sua scelta altrimenti il voto verrà considerato nullo.",
                translations.getTranslation("it", "merlin.excel.validation_error.error_column_headname"));

        file = new File(OUT_DIR, "I18n-messages.xlsx");
        I18nExcelConverter excelConverter = new I18nExcelConverter(translations);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            excelConverter.write(outputStream);
        }

        // Reread file:
        excelConverter = new I18nExcelConverter(); // Clean translations.
        translations = excelConverter.getTranslations();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            excelConverter.importTranslations(inputStream, "I18n-message.xlsx");
        }
        assertEquals(4, translations.getKeys().size());
        assertEquals("In Blatt ''{0}'', Spalte {1}:''{2}'' und Zeile #{3}: {4}",
                translations.getTranslation("de", "merlin.excel.validation_error.display_all"));
        assertEquals("Almeno una lista non è valida. Modifichi la sua scelta altrimenti il voto verrà considerato nullo.",
                translations.getTranslation("it", "merlin.excel.validation_error.error_column_headname"));
    }
}
