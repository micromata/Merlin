package de.micromata.merlin.app.javafx;

import de.micromata.merlin.app.rest.FilesystemBrowserRest;
import de.micromata.merlin.app.updater.AppUpdater;
import de.micromata.merlin.app.updater.UpdateInfo;
import de.micromata.merlin.server.RunningMode;
import de.micromata.merlin.server.Version;
import de.micromata.merlin.server.jetty.JettyServer;
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
import org.apache.log4j.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.TimerTask;

public class DesktopMain extends Application {
    private static Logger log = LoggerFactory.getLogger(DesktopMain.class);
    private JettyServer server;
    private static DesktopMain main;
    private Stage stage;
    private java.util.Timer timer;
    private static Thread updateThread;
    private boolean shutdownInProgress;

    public static void main(String[] args) {
        Version version = Version.getInstance();
        RunningMode.setServerType(RunningMode.ServerType.DESKTOP);
        RunningMode.logMode();
        try {
            org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
            FileAppender appender = (FileAppender) rootLogger.getAppender("file");
            String logFilename = appender.getFile();
            File logFile = logFilename != null ? new File(logFilename) : null; // On Windows logFilename is null!
            if (logFile == null || !logFile.canWrite()) {
                logFile = new File(System.getProperty("java.io.tmpdir"), "merlin.log");
                appender.setFile(logFile.getAbsolutePath());
                appender.activateOptions();
            }
            log.info("Writing logs to " + logFile.getAbsolutePath());
        } catch (Exception ex) {
            log.error("Can't detect file logger: " + ex.getMessage(), ex);
        }
        log.info("Current working directory: " + new File(".").getAbsolutePath());
        log.info("Using Java version: " + System.getProperty("java.version"));
        updateThread = new Thread() {
            @Override
            public void run() {
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
            }
        };
        updateThread.start();
        launch(args);
    }

    public static DesktopMain getInstance() {
        return main;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        main = this;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(DesktopMain.class.getResource("/Main.fxml"));
        ClassLoader cl = this.getClass().getClassLoader();
        Parent root;
        try {
            root = (Pane) loader.load();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return;
        }

        if (RunningMode.getOSType() == RunningMode.OSType.MAC_OS) {
            URL iconURL = DesktopMain.class.getResource("/icon.png");
            Image image = new ImageIcon(iconURL).getImage();
            // com.apple.eawt.Application.getApplication().setDockIconImage(image);
            // Use reflection for avoiding compiling errors on non Mac OS development systems.
            try {
                Class<?> c = Class.forName("com.apple.eawt.Application");
                Method method = c.getMethod("getApplication");
                method.setAccessible(true);
                Object application = method.invoke(null);
                method = application.getClass().getMethod("setDockIconImage", Image.class);
                method.setAccessible(true);
                method.invoke(application, image);
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
        final long startTime = System.currentTimeMillis();
        timer.schedule(new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    // RunLater is important: ui elements must be accessed on the fxApplication thread.
                    long duration = (System.currentTimeMillis() - startTime) / 1000;
                    String formattedDuration = String.format(
                            "%d:%02d:%02d",
                            duration / 3600,
                            (duration % 3600) / 60,
                            duration % 60);
                    statusTextField.setText(statusText + ": " + formattedDuration + statusDots[i]);
                    statusTextField.getStyleClass().clear();
                    statusTextField.getStyleClass().add("status-" + i);
                });
                if (++i > 5) {
                    i = 0;
                }
            }
        }, 500L, 333L);

        Text text = (Text) scene.lookup("#versionText");
        text.setText(context.getString("merlin.server.app.versionText") + " " + Version.getInstance().getShortVersion());
        stage.setResizable(false);
        stage.show();
        server = de.micromata.merlin.server.Main.startUp(FilesystemBrowserRest.class.getPackage().getName());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        shutdownInProgress = true;
        log.info("Stopping Java FX application.");
        timer.cancel();
        updateThread.interrupt(); // Thread hangs if now connection to update server. Force to stop.
        server.stop();
    }

    public boolean isShutdownInProgress() {
        return shutdownInProgress;
    }

    public Stage getStage() {
        return stage;
    }

    void openBrowser() {
        getHostServices().showDocument(server.getUrl());
    }

    private String[] statusDots = new String[]{"...", " ...", "  ...", "   ...", "  ...", " ..."};
}
