package pl.jgwozdz.utils.xmlscan.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import pl.jgwozdz.utils.xmlscan.javafx.model.FileLoader;

import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 */
public class FileLoaderController implements Initializable {
    public AnchorPane fileLoaderPane;
    public ListView<Path> fileList;
    public TextField dirField;
    private ObjectProperty<Path> selectedFile = new SimpleObjectProperty<>(null);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dirField.textProperty().addListener(this::updateFileList);
        fileList.getSelectionModel().getSelectedItems().addListener(this::updateSelectedFile);
    }

    void updateSelectedFile(ListChangeListener.Change<? extends Path> change) {
        Path dir = Paths.get(dirField.getText());

        while(change.next()) {
            if (change.wasRemoved() && !change.wasAdded()) {
                selectedFile.setValue(null);
            }
            if (change.wasAdded()) {
                change.getAddedSubList().stream()
                        .findFirst()
                        .map(dir::resolve)
                        .ifPresent(selectedFile::setValue);
            }
        }
    }

    void updateFileList(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        fileList.itemsProperty().get().clear();
        Path dir = Paths.get(newValue);
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, Files::isRegularFile)){
            directoryStream.forEach(file -> result.add(dir.relativize(file)));
        } catch (Exception e) {
            System.out.println(dir + " unreadable: " + e);
            return;
        }
        fileList.itemsProperty().get().addAll(result);
    }

    void setModel(FileLoader model) {
        dirField.textProperty().bindBidirectional(model.directoryToScanProperty());
        fileList.itemsProperty().bindBidirectional(model.filesProperty());
        selectedFile.bindBidirectional(model.selectedFileProperty());
    }

//    public FileLoader getModel() {
//        return model;
//    }
}
