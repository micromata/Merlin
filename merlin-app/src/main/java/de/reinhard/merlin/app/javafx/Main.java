package de.reinhard.merlin.app.javafx;

import de.reinhard.merlin.app.Version;
import de.reinhard.merlin.app.jetty.JettyServer;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.app.updater.AppUpdater;
import de.reinhard.merlin.app.updater.UpdateInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private JettyServer server;
    private static Main main;
    private Stage stage;

    public static void main(String[] args) {
        Version version = Version.getInstance();
        log.info("Starting " + version.getAppName() + " " + version.getVersion() + ", build time: "
                + version.getBuildDate() + " (UTC: " + version.getBuildDateUTC() + "), mode: " + RunningMode.getMode());
        log.info("Current working directory: " + new File(".").getAbsolutePath());
        log.info("Using Java version: " + System.getProperty("java.version"));
        new Thread(() -> {
            new Thread(() -> {
                try {
                    if (!RunningMode.isDevelopmentMode()) {
                        AppUpdater.getInstance().checkUpdate();
                    } else {
                        // No update mechanism in development mode.
                        UpdateInfo.getInstance().setDevelopmentTestData(); // Only for testing.
                    }
                } catch (Exception ex) {
                    // Don't stop application due to failed update check.
                    log.error("Exception while checking update: " + ex.getMessage(), ex);
                }
            }).start();
        }).start();
        launch(args);
    }

    static Main getInstance() {
        return main;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        log.info("Starting Java FX application in mode: " + RunningMode.getMode());
        main = this;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/Main.fxml"));
        ClassLoader cl = this.getClass().getClassLoader();
        Parent root;
        try {
            root = (Pane) loader.load();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return;
        }

        if (RunningMode.getOSType() == RunningMode.OS_TYPE.MAC_OS) {
            URL iconURL = Main.class.getResource("/icon.png");
            Image image = new ImageIcon(iconURL).getImage();
            // com.apple.eawt.Application.getApplication().setDockIconImage(image);
            // Use reflection for avoiding compiling errors on non Mac OS development systems.
            try {
                Class<?> c = Class.forName("com.apple.eawt.Application");
                Object application = MethodUtils.invokeStaticMethod(c, "getApplication", (Object[]) null);
                MethodUtils.invokeMethod(application, "setDockIconImage", image);
            } catch (Exception ex) {
                log.error("Can't call com.apple.eawt.Application.getApplication().setDockIconImage(image): " + ex.getMessage(), ex);
            }
        }

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Merlin Server");

        Context context = Context.instance();
        Tooltip tooltip = new Tooltip();
        tooltip.setText(context.getString("merlin.server.app.openBrowser.button.tooltip"));
        Button startButton = (Button) scene.lookup("#startButton");
        startButton.setText(context.getString("merlin.server.app.openBrowser.button"));
        startButton.setTooltip(tooltip);

        Text text = (Text) scene.lookup("#serverStatusText");
        text.setText(context.getString("merlin.server.app.serverStatusText"));
        text = (Text) scene.lookup("#versionText");
        text.setText(context.getString("merlin.server.app.versionText") + " " + Version.getInstance().getVersion());

        stage.setResizable(false);
        stage.show();
        server = new JettyServer();
        server.start();
        RunningMode.setRunning(true);
        try {
            Storage.getInstance().onStartup();
        } catch (Exception ex) {
            // Don't stop application due to failed update check.
            log.error("Error while loading storage data: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        log.info("Stopping Java FX application.");
        server.stop();
    }

    public Stage getStage() {
        return stage;
    }

    void openBrowser() {
        getHostServices().showDocument(server.getUrl());
    }
}
