package de.reinhard.merlin.app.javafx;

import de.reinhard.merlin.app.Version;
import de.reinhard.merlin.app.jetty.JettyServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
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
                + version.getBuildDate() + " (UTC: " + version.getBuildDateUTC() + ").");
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
        stage.setTitle("Merlin");
        stage.setResizable(false);
        stage.show();
        server = new JettyServer();
        server.start();
        RunningMode.setRunning(true);
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
