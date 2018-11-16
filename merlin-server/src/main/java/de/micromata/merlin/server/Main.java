package de.micromata.merlin.server;

import de.micromata.merlin.server.jetty.JettyServer;
import de.micromata.merlin.server.storage.Storage;
import de.micromata.merlin.server.user.SingleUserManager;
import de.micromata.merlin.server.user.UserManager;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static final Main main = new Main();

    private JettyServer server;
    private boolean shutdownInProgress;

    private Main() {
    }

    public static void main(String[] args) {
        main._start(args);
    }

    public static JettyServer startUp(String... restPackageNames) {
        return main._startUp(restPackageNames);
    }

    public static void shutdown() {
        main._shutdown();
    }


    private void _start(String[] args) {
        // create Options object
        Options options = new Options();
        options.addOption("p", "port", true, "The default port for the web server.");
        options.addOption("q", "quiet", false, "Don't open browser automatically.");
        options.addOption("h", "help", false, "Print this help screen.");
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption('h')) {
                printHelp(options);
                return;
            }
            if (line.hasOption('p')) {
                // initialise the member variable
                String portString = line.getOptionValue("p");
                try {
                    int port = Integer.parseInt(portString);
                    if (port < 1 || port > 65535) {
                        System.err.println("Port outside range.");
                        return;
                    }
                    ConfigurationHandler.getDefaultConfiguration().setPort(port);
                } catch (NumberFormatException ex) {
                    printHelp(options);
                    return;
                }
            }
            RunningMode.setServerType(RunningMode.ServerType.SERVER);
            RunningMode.logMode();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    main._shutdown();
                }
            });

            JettyServer server = startUp();
            if (!line.hasOption('q')) {

                try {
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(server.getUrl()));
                } catch (Exception ex) {
                    log.info("Can't open web browser: " + ex.getMessage());
                }
            }
        } catch (ParseException ex) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + ex.getMessage());
            printHelp(options);
        }
    }

    private JettyServer _startUp(String... restPackageNames) {
        server = new JettyServer();
        server.start(restPackageNames);

        UserManager.setUserManager(new SingleUserManager());

        new Thread(() -> {
            try {
                // Preload storage already in the background for faster access for the user after opening the client.
                Storage.getInstance().onStartup();
            } catch (Exception ex) {
                log.error("Error while loading storage data: " + ex.getMessage(), ex);
            }
        }).start();
        return server;
    }

    private void _shutdown() {
        synchronized (this) {
            if (shutdownInProgress == true) {
                // Another thread already called this method. There is nothing further to do.
                return;
            }
            shutdownInProgress = true;
        }
        log.info("Shutting down Merlin web server...");
        server.stop();
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("merlin-server", options);
    }
}
