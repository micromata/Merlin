package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static final String generatedDir = "generated";
    private static final String sourcesDir = "sources";

    private static final Main main = new Main();
    private I18nConverter i18nConverter;
    private Dictionary dictionary;
    private String basename;
    private boolean keysOnly;

    private Main() {
    }

    public static void main(String[] args) {
        main._start(args);
    }

    private void _start(String[] args) {
        // create Options object
        Options options = new Options();
        options.addOption("b", "basename", true, "The base name of the output files. Default is 'i18n-generated'.");

        Option option = new Option("r", "read", true,
                "Reads the translations of the given filename. Doesn't overwrite existing translations and create new keys if not exist.");
        option.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(option);

        option = new Option("ro", "read-overwrite", true,
                "Reads the translations of the given filename. Does overwrite existing translations and create new keys if not exist.");
        option.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(option);

        option = new Option("rm", "read-merge", true,
                "Reads the translations of the given filename. Doesn't overwrite existing translations and doesn't create new keys if not exist.");
        option.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(option);

        options.addOption("ko", "keys-only", false,
                "Don't export the translations for the json file, only the keys.");

        options.addOption("nz", "no-zip", false,
                "Don't write files to zip archive, write files directly.");

        options.addOption("d", "diff", true,
                "Reads the given dictionary (*-dictionary.json or any translation file) and shows the differences of the current read translation source files (in generated Excel file).");

        options.addOption("h", "help", false, "Print this help screen.");

        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption('h')) {
                printHelp(options);
                return;
            }
            this.basename = "i18n-generated";
            if (line.hasOption('b')) {
                this.basename = line.getOptionValue("b");
            }
            this.keysOnly = false;
            if (line.hasOption("ko")) {
                this.keysOnly = true;
            }
            this.dictionary = new Dictionary();
            if (line.hasOption("d")) {
                String filename = line.getOptionValue("d");
                log.info("Reading other dictionary for detecting differences: " + filename);
                if (filename.endsWith("dictionary.json")) {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(new FileReader(new File(filename)), writer);
                    ObjectMapper mapper = new ObjectMapper();
                    Dictionary diffDictionary = mapper.readValue(writer.toString(), Dictionary.class);
                    this.dictionary.setDiffDictionary(diffDictionary);
                } else {
                    Dictionary diffDictionary = new Dictionary();
                    I18nConverter diffConverter = new I18nConverter(diffDictionary);
                    diffConverter.importTranslations(new File(filename));
                    this.dictionary.setDiffDictionary(diffDictionary);
                }
            }

            this.i18nConverter = new I18nConverter(this.dictionary);
            Option[] parsedOptions = line.getOptions();
            for (Option parsedOption : parsedOptions) {
                if (!"r".equals(parsedOption.getOpt()) &&
                        !"ro".equals(parsedOption.getOpt()) &&
                        !"rm".equals(parsedOption.getOpt())) {
                    continue;
                }
                String[] optionFiles = parsedOption.getValues();
                for (String filename : optionFiles) {
                    if ("r".equals(parsedOption.getOpt())) {
                        dictionary.setOverwriteExistingTranslations(false);
                                dictionary.setCreateKeyIfNotPresent(true);
                    } else if ("ro".equals(parsedOption.getOpt())) {
                        dictionary.setOverwriteExistingTranslations(true);
                        dictionary.setCreateKeyIfNotPresent(true);
                    } else if ("rm".equals(parsedOption.getOpt())) {
                        dictionary.setOverwriteExistingTranslations(false);
                        dictionary.setCreateKeyIfNotPresent(false);
                    } else {
                        log.error("Unsupported option: " + option.getValue());
                    }
                    File file = new File(filename);
                    dictionary.log("*** Reading with option -" + parsedOption.getLongOpt() + ": " + file.getAbsolutePath());
                    i18nConverter.importTranslations(file);
                }
            }
            String[] argFiles = line.getArgs();
            if (argFiles != null && argFiles.length > 0) {
                dictionary.setOverwriteExistingTranslations(false);
                dictionary.setCreateKeyIfNotPresent(true);
                for (String filename : argFiles) {
                    File file = new File(filename);
                    dictionary.log("*** Reading : " + file.getAbsolutePath());
                    i18nConverter.importTranslations(file);
                }
            }
            if (dictionary.getKeys().size() == 0) {
                log.info("No translations imported....");
                printHelp(options);
            } else {
                if (line.hasOption("nz")) {
                    writeFiles();
                } else {
                    writeZip();
                }
            }
        } catch (IOException ex) {
            log.error("Error while procesing files: " + ex.getMessage());
        } catch (ParseException ex) {
            // oops, something went wrong
            log.error("Parsing failed.  Reason: " + ex.getMessage());
            printHelp(options);
        }
    }

    private void writeZip() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat iso = new SimpleDateFormat("yyyy-MM-dd_HH-mm"); // Quoted "Z" to indicate UTC, no timezone offset
        iso.setTimeZone(tz);

        File zipFile = new File(iso.format(new Date()) + "-" + basename + ".zip");
        log.info("Writing file " + zipFile.getAbsolutePath());
        try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
            writeFiles(dictionary, basename, keysOnly, zipOut);
        } catch (IOException ex) {
            log.error("Error while writing zip archive '" + zipFile.getAbsolutePath() + "': " + ex.getMessage(), ex);
        }
    }

    private void writeFiles(Dictionary dictionary, String basename, boolean keysOnly, ZipOutputStream zipOut) throws IOException {
        writeSources(zipOut);
        writeJson(zipOut);
        writeJsonTree(zipOut);
        writeExcel(zipOut);
        writeProperties(zipOut);
        writeLogFile(zipOut);
        writeDictionary(zipOut);
    }

    private void writeJson(ZipOutputStream zipOut) throws IOException {
        File file = new File(generatedDir, basename + ".json");
        logAddingCreatingFile(null, file, zipOut);
        if (zipOut != null) {
            zipOut.putNextEntry(new ZipEntry(file.toString()));
            I18nJsonConverter jsonConverter = new I18nJsonConverter(dictionary);
            jsonConverter.setKeysOnly(keysOnly);
            jsonConverter.write(new OutputStreamWriter(zipOut, Charset.forName("UTF-8")));
        } else {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")))) {
                I18nJsonConverter jsonConverter = new I18nJsonConverter(dictionary);
                jsonConverter.setKeysOnly(keysOnly);
                jsonConverter.write(writer);
            }
        }
    }

    private void writeJsonTree(ZipOutputStream zipOut) throws IOException {
        File file;
        for (String lang : dictionary.getUsedLangs()) {
            File dir = null;
            if (lang.length() > 0) {
                dir = new File(generatedDir, lang);
                if (zipOut == null && !dir.exists()) {
                    dir.mkdir();
                }
                file = new File(dir, "translations.json");
            } else {
                file = new File(generatedDir, "translation.json");
            }
            logAddingCreatingFile(dir, file, zipOut);
            if (zipOut != null) {
                zipOut.putNextEntry(new ZipEntry(file.toString()));
                new I18nJsonTreeConverter(dictionary).write(lang, new OutputStreamWriter(zipOut, Charset.forName("UTF-8")));
            } else {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")))) {
                    new I18nJsonTreeConverter(dictionary).write(lang, writer);
                }
            }
        }
    }

    private void writeExcel(ZipOutputStream zipOut) throws IOException {
        File file = new File(generatedDir, basename + ".xlsx");
        logAddingCreatingFile(null, file, zipOut);
        if (zipOut != null) {
            zipOut.putNextEntry(new ZipEntry(file.toString()));
            new I18nExcelConverter(dictionary).write(zipOut);
        } else {
            try (OutputStream outputStream = new FileOutputStream(file)) {
                new I18nExcelConverter(dictionary).write(outputStream);
            }
        }
    }

    private void writeProperties(ZipOutputStream zipOut) throws IOException {
        File file;
        for (String lang : dictionary.getUsedLangs()) {
            if (lang.length() > 0) {
                file = new File(generatedDir, basename + "_" + lang + ".properties");
            } else {
                file = new File(generatedDir, basename + ".properties");
            }
            logAddingCreatingFile(null, file, zipOut);
            if (zipOut != null) {
                zipOut.putNextEntry(new ZipEntry(file.toString()));
                new I18nPropertiesConverter(dictionary).write(lang, new OutputStreamWriter(zipOut, Charset.forName("UTF-8")));
            } else {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")))) {
                    new I18nPropertiesConverter(dictionary).write(lang, writer);
                }
            }
        }
    }

    private void writeDictionary(ZipOutputStream zipOut) throws IOException {
        File file = new File(basename + "-dictionary.json");
        logAddingCreatingFile(null, file, zipOut);
        String json = toJson(dictionary);
        if (zipOut != null) {
            zipOut.putNextEntry(new ZipEntry(file.toString()));
            zipOut.write(json.getBytes());
        } else {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")))) {
                writer.write(json);
            }
        }
    }

    private String toJson(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }
    }

    private void writeSources(ZipOutputStream zipOut) throws IOException {
        if (zipOut == null) {
            return;
        }
        for (Map.Entry<File, File> entry : i18nConverter.getImportedFiles().entrySet()) {
            File source = entry.getKey();
            File destFile = new File(sourcesDir, entry.getValue().toString());
            logAddingCreatingFile(new File(sourcesDir), destFile, zipOut);
            if (zipOut != null) {
                zipOut.putNextEntry(new ZipEntry(destFile.toString()));
                IOUtils.copy(new FileReader(source), zipOut, Charset.forName("UTF-8"));
            } else {
                // Do nothing.
            }
        }
    }

    private void writeLogFile(ZipOutputStream zipOut) throws IOException {
        File file = new File(basename + ".log");
        logAddingCreatingFile(null, file, zipOut);
        if (zipOut != null) {
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            zipOut.write(dictionary.getLogging().getBytes());
        } else {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")))) {
                writer.write(dictionary.getLogging());
            }
        }
    }


    private void logAddingCreatingFile(File dir, File file, ZipOutputStream zipOut) {
        if (zipOut != null) {
            String subdir = dir != null ? dir.getName() + "/" : "";
            log.info("Adding file " + subdir + file.getName());
        } else {
            log.info("Creating file " + file.getAbsolutePath());
        }
    }

    private void writeFiles() throws IOException {
        writeFiles(dictionary, basename, keysOnly, null);
    }


    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("merlin-i18n-converter [OPTIONS] [DIR/FILE1] [DIR/FILE2]...",
                "Read i18n translations of different formats, merges and writes the translations to different foramts.",
                options,
                "The optional given files [DIR/FILE1] [DIR/FILE2]... will be read with the flag -r.\n\n"
                        + "Further information on: https://github.com/micromata/Merlin/tree/master/merlin-i18n-converter");
    }
}
