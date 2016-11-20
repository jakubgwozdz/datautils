package pl.jgwozdz.utils.xmlscan.javafx.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;

/**
 *
 */
public class FileLoader {

    private final StringProperty directoryToScan = new SimpleStringProperty();
    private final ListProperty<Path> files = new SimpleListProperty<>();

    public String getDirectoryToScan() {
        return directoryToScan.get();
    }

    public StringProperty directoryToScanProperty() {
        return directoryToScan;
    }

    public void setDirectoryToScan(String directoryToScan) {
        this.directoryToScan.set(directoryToScan);
    }

    public ObservableList<Path> getFiles() {
        return files.get();
    }

    public ListProperty<Path> filesProperty() {
        return files;
    }

    public void setFiles(ObservableList<Path> files) {
        this.files.set(files);
    }
}
