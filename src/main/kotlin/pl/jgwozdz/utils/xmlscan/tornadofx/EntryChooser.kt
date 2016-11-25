package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.Priority
import org.w3c.dom.Element
import tornadofx.*


class EntryChooserController : Controller() {

    val entries: ObservableList<Element> = FXCollections.observableArrayList<Element>()
    val selectedEntry = SimpleObjectProperty<Element>()

}



class EntryChooserView : View() {

    val ctrl : EntryChooserController by inject()

    override val root = anchorpane {
        prefWidth = 200.0
        prefHeight = 400.0
        vbox(spacing = 5.0) {
            allAnchors = 5.0
            label("Found entries: ")
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vgrow = Priority.ALWAYS
                listview(ctrl.entries) {
                    bindSelected(ctrl.selectedEntry)
                }.cellFormat {
                    text = it.textContent
                }
            }
        }

    }

}
