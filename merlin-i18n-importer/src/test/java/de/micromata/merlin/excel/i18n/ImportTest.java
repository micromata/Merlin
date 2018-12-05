package de.micromata.merlin.excel.i18n;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImportTest {
    @Test
    void importPropertiesTest() throws IOException, FileNotFoundException {
        I18nPropertiesImporter i18nImporter = new I18nPropertiesImporter();
        File file = new File("test-data", "i18n_messages_de.properties");
        i18nImporter.importTranslations("de", new FileReader(file));
        Translations translations = i18nImporter.getTranslations();
        assertEquals(4, translations.getKeys().size());
        assertEquals("In Blatt ''{0}'', Spalte {1}:''{2}'' und Zeile #{3}: {4}", translations.getTranslation("de", "merlin.excel.validation_error.display_all"));
    }

    @Test
    void importJsonTest() throws IOException, FileNotFoundException {
        I18nJsonImporter i18nImporter = new I18nJsonImporter();
        File file = new File("test-data", "i18n_messages.json");
        i18nImporter.importTranslations(new FileReader(file));
        Translations translations = i18nImporter.getTranslations();
        assertEquals(3, translations.getKeys().size());
        assertEquals("", translations.getTranslation("de", "merlin.excel.validation_error.display_all"));

        assertEquals("Almeno una lista non è valida. Modifichi la sua scelta altrimenti il voto verrà considerato nullo.", translations.getTranslation("it", "merlin.excel.validation_error.error_column_headname"));
    }
}
