package pl.jgwozdz.utils.xmlscan.javafx;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import pl.jgwozdz.utils.xmlscan.javafx.model.MainAppModel;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 *
 */
public class MainAppController implements Initializable {
    @FXML
    private BorderPane rootPane;

    @FXML
    private FileChooserController fileChooserController;

    @FXML
    private EntryChooserController entryChooserController;

    @FXML
    private AnchorPane resultPane;

    @FXML
    private AnchorPane fileChooser;
    @FXML
    private AnchorPane entryChooser;

    private Property<Path> selectedFile = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedFile.addListener((observable, oldValue, newValue) -> changeToFile(newValue));
    }

    protected void changeToFile(Path newValue) {
        System.out.println("pl.jgwozdz.utils.xmlscan.javafx.MainAppController.changeToFile("+newValue+")");
        entryChooserController.currentFile.setValue(newValue);
    }

    public void setModel(MainAppModel mainAppModel) {
        fileChooserController.setModel(mainAppModel.fileChooserModel);
        entryChooserController.setModel(mainAppModel.entryChooserModel);
        mainAppModel.fileChooserModel.selectedFileProperty().bindBidirectional(selectedFile);
    }
}
