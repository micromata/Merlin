package de.micromata.merlin.excel.i18n;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        options.addOption("o", "output", true, "The base name of the output files. Default is 'i18n.'.");
        options.addOption("h", "help", false, "Print this help screen.");
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption('h')) {
                printHelp(options);
                return;
            }
            String basename = "i18n.";
            if (line.hasOption('o')) {
                basename = line.getOptionValue("o");
            }
            String[] files = line.getArgs();
            if (files == null ||files.length == 0) {
                printHelp(options);
                return;
            }

        } catch (ParseException ex) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + ex.getMessage());
            printHelp(options);
        }
    }


    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("merlin-i18n-converter [OPTIONS] [FILE1] [FILE2]...", options);
    }
}
