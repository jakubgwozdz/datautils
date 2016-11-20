package pl.jgwozdz.utils.xmlscan.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import pl.jgwozdz.utils.xmlscan.javafx.model.FileChooserModel;

import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 *
 */
public class FileChooserController implements Initializable {
    public ListView<Path> fileList;
    public TextField dirField;
    private ObjectProperty<Path> currentDir = new SimpleObjectProperty<>(null);
    private ObjectProperty<Path> selectedFile = new SimpleObjectProperty<>(null);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentDir.addListener(this::updateFileList);
        fileList.getSelectionModel().getSelectedItems().addListener(this::updateSelectedFile);
        StringConverter<Path> pathFormatter = new StringConverter<Path>() {
            @Override
            public String toString(Path object) {
                return Optional.ofNullable(object).map(Path::toString).orElse(null);
            }

            @Override
            public Path fromString(String string) {
                return Optional.ofNullable(string).map(Paths::get).orElse(null);
            }
        };
        dirField.textProperty().bindBidirectional(currentDir, pathFormatter);
    }

    void updateSelectedFile(ListChangeListener.Change<? extends Path> change) {

        while (change.next()) {
            if (change.wasRemoved() && !change.wasAdded()) {
                selectedFile.setValue(null);
            }
            if (change.wasAdded()) {
                change.getAddedSubList().stream()
                        .findFirst()
                        .map(currentDir.getValue()::resolve)
                        .ifPresent(selectedFile::setValue);
            }
        }
    }

    void updateFileList(ObservableValue<? extends Path> observable, Path oldValue, Path newValue) {
        fileList.itemsProperty().get().clear();
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(newValue,
                path -> Files.isRegularFile(path) &&
                        path.getFileName().toString().toLowerCase().endsWith(".xml"))) {
            directoryStream.forEach(file -> result.add(newValue.relativize(file)));
        } catch (Exception e) {
            System.out.println(newValue + " unreadable: " + e);
            return;
        }
        fileList.itemsProperty().get().addAll(result);
    }

    void setModel(FileChooserModel model) {
        currentDir.bindBidirectional(model.directoryToScanProperty());
        fileList.itemsProperty().bindBidirectional(model.filesProperty());
        selectedFile.bindBidirectional(model.selectedFileProperty());
    }

//    public FileLoader getModel() {
//        return model;
//    }
}
