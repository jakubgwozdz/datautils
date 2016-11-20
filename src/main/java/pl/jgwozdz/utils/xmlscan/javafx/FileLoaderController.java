package pl.jgwozdz.utils.xmlscan.javafx;

import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import pl.jgwozdz.utils.xmlscan.javafx.model.FileLoader;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public class FileLoaderController implements Initializable {
    public AnchorPane fileLoaderPane;
    public ListView fileList;
    public TextField dirField;

//    private FileLoader model = new FileLoader();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        dirField.textProperty().bindBidirectional(model.directoryToScanProperty());
    }

    public void setModel(FileLoader model) {
//        this.model = model;
        dirField.textProperty().bindBidirectional(model.directoryToScanProperty());
    }

//    public FileLoader getModel() {
//        return model;
//    }
}
