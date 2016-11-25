package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.layout.Priority.ALWAYS
import org.w3c.dom.Element
import tornadofx.*


class EntryChooserController : Controller() {

    val entries: ObservableList<Element> = FXCollections.observableArrayList<Element>()
    val filteredEntries: FilteredList<Element> = FilteredList(entries, { it: Element? -> true })
    val filterBy = SimpleStringProperty()
    val selectedEntry = SimpleObjectProperty<Element>()

    init {
        reportBlockEntry()
        filterBy.addListener { observable, oldValue, newValue ->
            filteredEntries.setPredicate { element ->
                when {
                    newValue.isNullOrBlank() -> true
                    element.textContent.contains(newValue) -> true
                    else -> false
                }
            }
        }
    }


}


class EntryChooserView : View() {

    val ctrl: EntryChooserController by inject()

    override val root = anchorpane {
        prefWidth = 200.0
        prefHeight = 400.0
        vbox(spacing = 5.0) {
            allAnchors = 5.0
            label("Filter by:")
            textfield(ctrl.filterBy) { vgrow = ALWAYS }
            label("Found entries: ")
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vgrow = ALWAYS
                listview(ctrl.filteredEntries) {
                    bindSelected(ctrl.selectedEntry)
                }.cellFormat {
                    text = it.textContent
                }
            }
        }

    }

    init {
        reportBlockEntry()
    }

}
