package pl.jgwozdz.utils.xmlscan.javafx.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Element;

import java.nio.file.Path;

/**
 *
 */
public class EntryChooserModel {

    private final ListProperty<Element> entries = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Element> selectedEntry = new SimpleObjectProperty<>();

    public ObservableList<Element> getEntries() {
        return entries.get();
    }

    public ListProperty<Element> entriesProperty() {
        return entries;
    }

    public void setEntries(ObservableList<Element> entries) {
        this.entries.set(entries);
    }

    public Element getSelectedEntry() {
        return selectedEntry.get();
    }

    public ObjectProperty<Element> selectedEntryProperty() {
        return selectedEntry;
    }

    public void setSelectedEntry(Element selectedEntry) {
        this.selectedEntry.set(selectedEntry);
    }
}
