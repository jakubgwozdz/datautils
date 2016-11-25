package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.util.Callback
import pl.jgwozdz.utils.xmlscan.AnalyzedData
import pl.jgwozdz.utils.xmlscan.ScannedSingleRow
import pl.jgwozdz.utils.xmlscan.TagStats
import tornadofx.*

//class Record(row: ScannedSingleRow) {
//    val rowProperty = SimpleObjectProperty<ScannedSingleRow>(row)
//    var row by rowProperty
//}
//
class AnalyzedEntryController : Controller() {
    val analyzedData = SimpleObjectProperty<AnalyzedData>()
    internal val rows: ObservableList<ScannedSingleRow> = FXCollections.observableArrayList<ScannedSingleRow>()
    internal val columns: ObservableList<TagStats> = FXCollections.observableArrayList<TagStats>()

    init {
        reportBlockEntry()
        analyzedData.addListener { observableValue, old, new ->
            rows.setAll(new.scannedData.rows)
            columns.setAll(new.tagsStats)
        }
    }
}

class AnalyzedEntryView : View() {

    val ctrl: AnalyzedEntryController by inject()
    var tableView: TableView<ScannedSingleRow> by singleAssign()

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
                    selectionModel.selectionMode = SelectionMode.MULTIPLE
                }
            }
        }
    }

    init {
        reportBlockEntry()
        ctrl.columns.addListener { change: ListChangeListener.Change<out TagStats> ->
            while (change.next()) {
                if (change.wasRemoved() && !change.wasAdded()) {
                    tableView.columns.clear()
                }
                if (change.wasAdded()) {
                    val newColumns: List<TableColumn<ScannedSingleRow, String?>> = change.addedSubList
                            .filter { !it.allEntriesEqual }
                            .map { tagStats ->
                                TableColumn<ScannedSingleRow, String?>(tagStats.tagName).apply {

                                    // TODO: test with MapValueFactory
                                    cellValueFactory = Callback<TableColumn.CellDataFeatures<ScannedSingleRow, String?>, ObservableValue<String?>> { features ->
                                        ReadOnlyStringWrapper(features.value.values[tagStats.tagName]).readOnlyProperty
                                    }
                                    isSortable = false
                                    cellFormat {
                                        text = it
                                        textFill = if (it == tagStats.pivotValue) Color.LIGHTSLATEGREY.brighter().brighter() else Color.BLACK
                                        alignment = if (tagStats.numbersOnly) Pos.CENTER_RIGHT else Pos.CENTER_LEFT
                                    }
                                }
                            }
                    tableView.columns.setAll(newColumns)
                }
            }
        }
    }
}

