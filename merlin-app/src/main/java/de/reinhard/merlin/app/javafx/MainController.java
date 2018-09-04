package de.reinhard.merlin.app.javafx;

import de.reinhard.merlin.csv.CSVParser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainController {
    private Logger log = LoggerFactory.getLogger(CSVParser.class);
    @FXML
    private Button startButton;

    public MainController() {
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void printOutput() {
       log.info("Button");
    }
}
