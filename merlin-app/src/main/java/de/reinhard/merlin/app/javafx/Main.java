package de.reinhard.merlin.app.javafx;

import de.reinhard.merlin.app.RunningMode;
import de.reinhard.merlin.app.Version;
import de.reinhard.merlin.app.jetty.JettyServer;
import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.app.updater.AppUpdater;
import de.reinhard.merlin.app.updater.UpdateInfo;
import de.reinhard.merlin.app.user.SingleUserManager;
import de.reinhard.merlin.app.user.UserManager;
import javafx.application.Application;
import javafx.application.Platform;
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
import java.util.TimerTask;

public class Main extends Application {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private JettyServer server;
    private static Main main;
    private Stage stage;
    private java.util.Timer timer;

    public static void main(String[] args) {
        Version version = Version.getInstance();
        log.info("Starting " + version.getAppName() + " " + version.getVersion() + ", build time: "
                + version.getBuildDate() + " (UTC: " + version.getBuildDateUTC() + "), mode: " + RunningMode.getMode());
        log.info("Current working directory: " + new File(".").getAbsolutePath());
        log.info("Using Java version: " + System.getProperty("java.version"));
        new Thread(() -> {
            try {
                if (!RunningMode.isDevelopmentMode()) {
                    AppUpdater.getInstance().checkUpdate();
                } else {
                    // No update mechanism in development mode.
                    UpdateInfo.getInstance().setDevelopmentTestData(); // Only for testing.
                }
            } catch (Exception ex) {
                log.error("Exception while checking update: " + ex.getMessage(), ex);
            }
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
        RunningMode.setServerType(RunningMode.ServerType.DESKTOP);
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

        if (RunningMode.getOSType() == RunningMode.OSType.MAC_OS) {
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

        final Text statusTextField = (Text) scene.lookup("#serverStatusText");
        final String statusText = context.getString("merlin.server.app.serverStatusText");
        timer = new java.util.Timer("Timer");
        timer.schedule(new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    // RunLater is important: ui elements must be accessed on the fxApplication thread.
                    statusTextField.setText(statusText + statusDots[i]);
                    statusTextField.getStyleClass().clear();
                    statusTextField.getStyleClass().add("status-" + i);
                });
                if (++i > 5) {
                    i = 0;
                }
            }
        }, 500L, 400L);

        Text text = (Text) scene.lookup("#versionText");
        text.setText(context.getString("merlin.server.app.versionText") + " " + Version.getInstance().getVersion());
        stage.setResizable(false);
        stage.show();
        server = new JettyServer();
        server.start();

        UserManager.setUserManager(new SingleUserManager());

        new Thread(() -> {
            try {
                // Preload storage already in the background for faster access for the user after opening the client.
                Storage.getInstance().onStartup();
            } catch (Exception ex) {
                log.error("Error while loading storage data: " + ex.getMessage(), ex);
            }
        }).start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        timer.cancel();
        log.info("Stopping Java FX application.");
        server.stop();
    }

    public Stage getStage() {
        return stage;
    }

    void openBrowser() {
        getHostServices().showDocument(server.getUrl());
    }

    private String[] statusDots = new String[] {"...", " ...", "  ...", "   ...", "  ...", " ..."};
}
