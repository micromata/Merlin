package de.reinhard.merlin.app.desktop;

import de.reinhard.merlin.app.jetty.JettyServer;
import de.reinhard.merlin.app.storage.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private Logger log = LoggerFactory.getLogger(Main.class);
    private JettyServer server;
    private static Main main = new Main();
    private MainFrame mainFrame;

    public static void main(String[] args) {
        getInstance().start();
    }

    static Main getInstance() {
        return main;
    }

    public void start() {
        log.info("Starting Java Swing application in mode: " + RunningMode.getMode());
        if (RunningMode.getMode() == RunningMode.Mode.TemplatesTest) {
            // Creating data for testing.
            TestData.create();
        }
        mainFrame = new MainFrame(this);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                main.stop();
            }
        });
        server = new JettyServer();
        server.start();
    }

    synchronized void stop() {
        log.info("Stopping Java Swing application.");
        server.stop();
        System.exit(0);
    }

    String getServerUrl() {
        return "http://localhost:" + server.getPort() + "/";
    }
}
