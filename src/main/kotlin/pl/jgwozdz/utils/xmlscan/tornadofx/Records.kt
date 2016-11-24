package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.util.Callback
import pl.jgwozdz.utils.xmlscan.ScannedData
import pl.jgwozdz.utils.xmlscan.ScannedSingleRow
import tornadofx.*

class Record(row: ScannedSingleRow) {
    val rowProperty = SimpleObjectProperty<ScannedSingleRow>(row)
    var row by rowProperty
}

class AnalyzedEntryController : Controller() {
    val rows: ObservableList<Record> = FXCollections.observableArrayList<Record>()
    val scannedData = SimpleObjectProperty<ScannedData>()

    init {
        scannedData.addListener { observableValue, old, new ->
            rows.setAll(new.rows.map(::Record)) }
    }
}

class AnalyzedEntryView : View() {

    val ctrl: AnalyzedEntryController by inject()
    var tableView: TableView<Record> by singleAssign()

    override val root = anchorpane {
        vbox {
            spacing = 5.0
            allAnchors = 5.0
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vgrow = Priority.ALWAYS
                tableView = tableview(ctrl.rows) {
                    allAnchors = 5.0
                    isEditable = false
                }
            }
        }
    }

    init {
        ctrl.rows.addListener { change: ListChangeListener.Change<out Record> ->
            while (change.next()) {
                if (change.wasRemoved() && !change.wasAdded()) {
                    tableView.columns.clear()
                }
                if (change.wasAdded()) {
                    change.addedSubList.firstOrNull()?.let {
                        val newColumns = mutableListOf<TableColumn<Record, String?>>()
                        it.row.values.forEach { tag ->
                            newColumns += TableColumn<Record, String?>(tag.key).apply {
                                cellValueFactory = Callback<TableColumn.CellDataFeatures<Record, String?>, ObservableValue<String?>> { features ->
                                    ReadOnlyStringWrapper(features.value.row.values[tag.key]).readOnlyProperty
                                }
                                isSortable = false
                            }


                        }
                        tableView.columns.setAll(newColumns)
                    }
                }
            }
        }
    }

}

