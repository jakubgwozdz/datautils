package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.Property
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
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
                }.cellFormat {
                    text = it.textContent
                }
            }
        }

    }

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

    private val reportListener = Foo()

    var data: EntryChooserModel = EntryChooserModel()
    set(value) {
        println("EntryChooserModel change")
        entries.removeListener(reportListener)
        field = value
        entries.addListener(reportListener)

    }

    val selectedEntry = bind(autocommit = true) { data.selectedEntryProperty }

    val entries: Property<ObservableList<Element>> = (bind(autocommit = true)  { data.entriesProperty }).apply {
        addListener (reportListener)
    }

}

class Foo : ChangeListener<ObservableList<Element>> {
    override fun changed(observable: ObservableValue<out ObservableList<Element>>?, oldValue: ObservableList<Element>?, newValue: ObservableList<Element>?) {
        println("EntryChooserViewModel changed from ${oldValue?.size} to ${newValue?.size}")
    }

}