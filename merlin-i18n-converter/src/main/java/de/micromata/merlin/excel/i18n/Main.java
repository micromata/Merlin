package de.micromata.merlin.excel.i18n;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static final Main main = new Main();

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

        options.addOption("h", "help", false, "Print this help screen.");

        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption('h')) {
                printHelp(options);
                return;
            }
            String basename = "i18n-generated";
            if (line.hasOption('b')) {
                basename = line.getOptionValue("b");
            }
            boolean keysOnly = false;
            if (line.hasOption("ko")) {
                keysOnly = true;
            }

            I18nConverter i18nConverter = new I18nConverter();
            Dictionary dictionary = i18nConverter.getTranslations();
            Option[] parsedOptions = line.getOptions();
            for (Option parsedOption : parsedOptions) {
                if ("b".equals(parsedOption.getOpt()) ||
                        "ko".equals(parsedOption.getOpt()) ||
                        "nz".equals(parsedOption.getOpt())) {
                    continue;
                }
                String[] files = parsedOption.getValues();
                for (String file : files) {
                    if ("r".equals(parsedOption.getOpt())) {
                        dictionary.setOverwriteExistingTranslations(false).setCreateKeyIfNotPresent(true);
                    } else if ("ro".equals(parsedOption.getOpt())) {
                        dictionary.setOverwriteExistingTranslations(true).setCreateKeyIfNotPresent(true);
                    } else if ("rm".equals(parsedOption.getOpt())) {
                        dictionary.setOverwriteExistingTranslations(false).setCreateKeyIfNotPresent(false);
                    } else {
                        log.error("Unsupported option: " + option.getValue());
                    }
                    i18nConverter.importTranslations(new File(file));
                }
            }
            String[] files = line.getArgs();
            if (files != null && files.length > 0) {
                dictionary.setOverwriteExistingTranslations(false).setCreateKeyIfNotPresent(true);
                for (String file : files) {
                    i18nConverter.importTranslations(new File(file));
                }
            }
            if (dictionary.getKeys().size() == 0) {
                log.info("No translations imported....");
                printHelp(options);
            } else {
                if (line.hasOption("nz")) {
                    writeFiles(dictionary, basename, keysOnly);
                } else {
                    writeZip(dictionary, basename, keysOnly);
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

    private static void writeZip(Dictionary dictionary, String basename, boolean keysOnly) {
        File zipFile = new File(basename + ".zip");
        log.info("Writing file " + zipFile.getAbsolutePath());
        try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
            writeFiles(dictionary, basename, keysOnly, zipOut);
        } catch (IOException ex) {
            log.error("Error while writing zip archive '" + zipFile.getAbsolutePath() + "': " + ex.getMessage(), ex);
        }
    }

    private static void writeFiles(Dictionary dictionary, String basename, boolean keysOnly, ZipOutputStream zipOut) throws IOException {
        File file = new File(basename + ".json");
        logAddingCreatingFile(null, file, zipOut);
        if (zipOut != null) {
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            new I18nJsonConverter(dictionary).setKeysOnly(keysOnly).write(new OutputStreamWriter(zipOut, Charset.forName("UTF-8")));
        } else {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")))) {
                new I18nJsonConverter(dictionary).setKeysOnly(keysOnly).write(writer);
            }
        }
        file = new File(basename + ".xlsx");
        logAddingCreatingFile(null, file, zipOut);
        if (zipOut != null) {
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            new I18nExcelConverter(dictionary).write(zipOut);
        } else {
            try (OutputStream outputStream = new FileOutputStream(file)) {
                new I18nExcelConverter(dictionary).write(outputStream);
            }
        }
        for (String lang : dictionary.getUsedLangs()) {
            if (lang.length() > 0) {
                file = new File(basename + "_" + lang + ".properties");
            } else {
                file = new File(basename + ".properties");
            }
            logAddingCreatingFile(null, file, zipOut);
            if (zipOut != null) {
                zipOut.putNextEntry(new ZipEntry(file.getName()));
                new I18nPropertiesConverter(dictionary).write(lang, new OutputStreamWriter(zipOut, Charset.forName("UTF-8")));
            } else {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")))) {
                    new I18nPropertiesConverter(dictionary).write(lang, writer);
                }
            }
        }
        for (String lang : dictionary.getUsedLangs()) {
            File dir = null;
            if (lang.length() > 0) {
                dir = new File(lang);
                if (zipOut == null && !dir.exists()) {
                    dir.mkdir();
                }
                file = new File(dir,"translation.json");
            } else {
                file = new File("translation.json");
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
        file = new File(basename + ".log");
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

    private static void logAddingCreatingFile(File dir, File file, ZipOutputStream zipOut) {
        if (zipOut != null) {
            String subdir = dir != null ? dir.getName() + "/" : "";
            log.info("Adding file " + subdir + file.getName());
        } else {
            log.info("Creating file " + file.getAbsolutePath());
        }
    }

    private static void writeFiles(Dictionary dictionary, String basename, boolean keysOnly) throws IOException {
        writeFiles(dictionary, basename, keysOnly, null);
    }


    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("merlin-i18n-converter [OPTIONS] [FILE1] [FILE2]...",
                "Read i18n translations of different formats, merges and writes the translations to different foramts.",
                options,
                "The optional given files [FILE1] [FILE2]... will be read with the flag -r.\n\n"
                        + "Further information on: https://github.com/micromata/Merlin/tree/master/merlin-i18n-converter");
    }
}
