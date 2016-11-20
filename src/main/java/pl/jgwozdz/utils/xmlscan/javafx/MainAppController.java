package pl.jgwozdz.utils.xmlscan.javafx;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 *
 */
public class MainAppController {
    @FXML
    public BorderPane rootPane;

    @FXML
    public FileLoaderController fileLoaderController;

    @FXML
    public AnchorPane resultPane;

    @FXML
    public AnchorPane fileLoader;
}
