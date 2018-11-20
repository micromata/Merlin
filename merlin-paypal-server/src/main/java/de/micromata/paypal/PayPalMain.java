package de.micromata.paypal;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class PayPalMain {
    private static Logger log = LoggerFactory.getLogger(PayPalMain.class);

    PayPalConfig paypalConfig;
    private static PayPalMain main;

    private JettyServer server;
    private boolean shutdownInProgress;

    public static void shutdown() {
        main._shutdown();
    }

    public static void main(String[] args) throws Exception {
        main = new PayPalMain();
        main._start(args);
    }


    private PayPalMain() {
    }

    public void _start(String[] args) throws IOException, PayPalRestException {
        //create Options object
        Options options = new Options();
        options.addOption("f", "file", true, "The properties file with the properties 'paypal.client_id' and 'paypal.secret'.");

        //options.addOption("q", "quiet", false, "Don't open browser automatically.");
        options.addOption("h", "help", false, "Print this help screen.");
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption('h')) {
                printHelp(options);
                return;
            }
            File file;
            if (line.hasOption('f')) {
                file = new File(line.getOptionValue('f'));
            } else {
                file = new File(System.getProperty("user.home"), ".merlin-paypal");
            }
            if (!file.exists()) {
                System.err.println("Please specify properties file with paypal paypalConfig or create this: " + file.getAbsolutePath());
                printPropertiesExampleFile();
                return;
            }
            paypalConfig = new PayPalConfig();
            paypalConfig.read(file);
            if (Utils.isBlank(paypalConfig.getClientId()) ||
                    Utils.isBlank(paypalConfig.getClientSecret())) {
                System.err.println("Please define properties in file '" + file.getAbsolutePath() + "':");
                printPropertiesExampleFile();
            }
            if (paypalConfig.getMode() == PayPalConfig.Mode.SANDBOX) {
                String accessToken = PayPalConnector.getAccessToken(paypalConfig);
                log.info("Access token successfully received: " + accessToken);
            }
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    main._shutdown();
                }
            });
            server = new JettyServer();
            server.start(paypalConfig, 8142);
        } catch (ParseException ex) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + ex.getMessage());
            printHelp(options);
        }

    }

    private void _shutdown() {
        synchronized (this) {
            if (shutdownInProgress == true) {
                // Another thread already called this method. There is nothing further to do.
                return;
            }
            shutdownInProgress = true;
        }
        log.info("Shutting down Merlin paypal server...");
        server.stop();
    }

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("merlin-paypal-main", options);
    }

    private void printPropertiesExampleFile() {
        System.err.println("Example file:");
        System.err.println("# Supported modes are sandbox (default) and live:");
        System.err.println(PayPalConfig.KEY_MODE + "=sandbox");
        System.err.println(PayPalConfig.KEY_CLIENT_ID + "=<YOUR APPLICATION CLIENT ID>");
        System.err.println(PayPalConfig.KEY_SECRET + "=<YOUR APPLICATION CLIENT SECRET>");
        System.err.println("# return url called by Paypal after successful payments:");
        System.err.println(PayPalConfig.KEY_RETURN_URL + "=" + PayPalConfig.DEMO_RETURN_URL);
        System.err.println("# cancel url called by Paypal after cancelled payments:");
        System.err.println(PayPalConfig.KEY_CANCEL_URL + "=" + PayPalConfig.DEMO_CANCEL_URL + ".>");
    }
}
