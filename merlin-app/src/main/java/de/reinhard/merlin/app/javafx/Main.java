package de.reinhard.merlin.app.javafx;

import de.reinhard.merlin.app.jetty.JettyServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main extends Application {
    private Logger log = LoggerFactory.getLogger(Main.class);
    private JettyServer server;
    private static Main main;

    public static void main(String[] args) {
        launch(args);
    }

    static Main getInstance() {
        return main;
    }

    @Override
    public void start(Stage stage) {
        log.info("Starting Java FX application.");
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

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Merlin");
        stage.setResizable(false);
        stage.show();
        server = new JettyServer();
        server.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        log.info("Stopping Java FX application.");
        server.stop();
    }

    void openBrowser() {
        getHostServices().showDocument("http://localhost:" + server.getPort() + "/");
    }
}
