package de.reinhard.merlin.app.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainController {
    private Logger log = LoggerFactory.getLogger(MainController.class);
    @FXML
    private Button startButton;

    public MainController() {
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void printOutput() {
        Main.getInstance().openBrowser();
    }
}
