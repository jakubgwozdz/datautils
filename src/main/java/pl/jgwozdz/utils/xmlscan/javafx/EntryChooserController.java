package pl.jgwozdz.utils.xmlscan.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.w3c.dom.Element;
import pl.jgwozdz.utils.xmlscan.XMLReporter;
import pl.jgwozdz.utils.xmlscan.javafx.model.EntryChooserModel;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 *
 */
public class EntryChooserController implements Initializable {

    public ListView<Element> entriesList;
    private ObjectProperty<Element> selectedEntry = new SimpleObjectProperty<>(null);
    public ObjectProperty<Path> currentFile = new SimpleObjectProperty<>(null);

    private ObjectProperty<XMLReporter> xmlReporter = new SimpleObjectProperty<>(null);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entriesList.setCellFactory(param -> new ListCell<Element>() {
            @Override
            protected void updateItem(Element item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getTextContent());
                } else {
                    setText("<null>");
                }
            }
        });
        currentFile.addListener(this::readFile);
    }

    protected void readFile(ObservableValue<? extends Path> observable, Path oldValue, Path newValue) {
        System.out.println("pl.jgwozdz.utils.xmlscan.javafx.EntryChooserController.readFile(" + newValue + ")");
        XMLReporter oldReporter = xmlReporter.getValue();
        if (oldReporter != null) oldReporter.close();
        if (newValue == null) {
            xmlReporter.setValue(null);
            return;
        }
        XMLReporter newReporter = new XMLReporter(newValue);
        xmlReporter.setValue(newReporter);

        entriesList.getItems().setAll(newReporter.getAllEntries());

    }

    void setModel(EntryChooserModel model) {
        entriesList.itemsProperty().bindBidirectional(model.entriesProperty());
        selectedEntry.bindBidirectional(model.selectedEntryProperty());
    }


}
