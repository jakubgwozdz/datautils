package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.layout.Priority
import org.w3c.dom.Element
import tornadofx.*

class EntryChooserView : View() {

    val model = EntryChooserViewModel()

    override val root = anchorpane {
        prefWidth = 200.0
        prefHeight = 400.0
        vbox {
            spacing = 5.0
            anchorpaneConstraints { leftAnchor = 5.0; rightAnchor = 5.0; topAnchor = 5.0; bottomAnchor = 5.0 }
            label("Found entries: ")
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vboxConstraints { vGrow = Priority.ALWAYS }
                listview(model.entries.value) {
                    bindSelected(model.selectedEntry)
                }
            }
        }

    }

}

class Entry {
    val elementProperty = SimpleObjectProperty<Element>()
    var element by elementProperty
}

class EntryModel : ItemViewModel<Entry>() {
    val element = bind { item?.elementProperty }
}


class EntryChooserModel() {
    val selectedEntryProperty = SimpleObjectProperty<Element>()
    var selectedEntry: Element? by selectedEntryProperty

    val entriesProperty = SimpleListProperty(FXCollections.observableArrayList<Element>())
    var entries by entriesProperty
}

class EntryChooserItemViewModel : ItemViewModel<EntryChooserModel>() {
    val selectedEntry = bind { item?.selectedEntryProperty }
    val entries = bind { item?.entriesProperty }
}


class EntryChooserViewModel() : ViewModel() {

    var data = EntryChooserModel()

    val selectedEntry = bind(autocommit = true) { data.selectedEntryProperty }

    val entries = bind { data.entriesProperty }

}