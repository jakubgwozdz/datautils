package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.Priority
import org.w3c.dom.Element
import tornadofx.*

class EntryToAnalyze(entry:Element) {
    val entryProperty = SimpleObjectProperty<Element>(entry)
    var entry by entryProperty
}

class EntryChooserController : Controller() {

    val entries: ObservableList<EntryToAnalyze> = FXCollections.observableArrayList<EntryToAnalyze>()

}

class EntryToAnalyzeModel : ItemViewModel<EntryToAnalyze>() {
    val entry = bind { item?.entryProperty }
}



class EntryChooserView : View() {

    val ctrl : EntryChooserController by inject()
    val entryToAnalyzeModel : EntryToAnalyzeModel by inject()
//    val model = EntryChooserViewModel()

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
                    bindSelected(entryToAnalyzeModel)
                }.cellFormat {
                    text = it.entry?.textContent
                }
            }
        }

    }

}
