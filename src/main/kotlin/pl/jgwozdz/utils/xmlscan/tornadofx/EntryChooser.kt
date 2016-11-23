package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.Property
import javafx.scene.layout.Priority
import org.w3c.dom.Element
import tornadofx.*

class EntryChooserView : View() {

//    val model = EntryChooserViewModel()

    override val root = anchorpane {
        prefWidth = 200.0
        prefHeight = 400.0
        vbox {
            spacing = 5.0
            anchorpaneConstraints { leftAnchor = 5.0; rightAnchor = 5.0; topAnchor = 5.0; bottomAnchor = 5.0 }
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vboxConstraints { vGrow = Priority.ALWAYS }
//                listview {}
            }
        }

    }

}

class EntryChooserData(selectedEntry:Element?=null) {
    var selectedEntry by property(selectedEntry)
    fun selectedEntryProperty() = getProperty(EntryChooserData::selectedEntry)

    init {
        selectedEntryProperty().addListener {
            observableValue, oldVal, newVal ->
            println("${(observableValue as Property).name} changed from '$oldVal' to '$newVal'")
        }
    }
}

class EntryChooserViewModel(var data : EntryChooserData) : ViewModel() {
    val selectedEntry = bind(autocommit = true) { data.selectedEntryProperty() }

}