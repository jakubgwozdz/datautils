package pl.jgwozdz.utils.xmlscan.tornadofx

import com.sun.javafx.scene.control.skin.TableViewSkin
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TablePosition
import javafx.scene.control.TableView
import javafx.scene.input.Clipboard
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.util.Callback
import org.controlsfx.control.Notifications
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFont
import org.controlsfx.glyphfont.GlyphFontRegistry
import pl.jgwozdz.utils.xmlscan.AnalyzedData
import pl.jgwozdz.utils.xmlscan.ScannedSingleRow
import pl.jgwozdz.utils.xmlscan.TagStats
import pl.jgwozdz.utils.xmlscan.XMLReporter
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
            columns.setAll(new.tagsStats)
            rows.setAll(new.scannedData.rows)
        }
    }
}

class AnalyzedEntryView : View() {

    val ctrl: AnalyzedEntryController by inject()
    var tableView: TableView<ScannedSingleRow> by singleAssign()
    val fontAwesome: GlyphFont? = GlyphFontRegistry.font("FontAwesome")

    override val root = anchorpane {
        vbox {
            spacing = 5.0
            allAnchors = 5.0
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vgrow = Priority.ALWAYS
                tableView = tableview(ctrl.rows) {
                    isEditable = false
                    selectionModel.isCellSelectionEnabled = true
                    selectionModel.selectionMode = SelectionMode.MULTIPLE
                    contextmenu {
                        menuitem("Expand selection to whole row(s)", "Ctrl+W", fontAwesome?.create(FontAwesome.Glyph.EXPAND), {
                            selectWholeRow()
                        })
                        menuitem("Copy as text report", null, fontAwesome?.create(FontAwesome.Glyph.COPY), {
                            copyReportToClipboard()
                        })
                    }


                }
            }
        }
    }

    fun copyReportToClipboard() {
        val selectionModel: TableView.TableViewSelectionModel<ScannedSingleRow> = tableView.selectionModel
        val selectedCells: ObservableList<TablePosition<Any, Any>> = selectionModel.selectedCells
        val tags = selectedCells.map { it.tableColumn }
                .distinct()
                .map { it.text }

        val report = XMLReporter(ctrl.analyzedData.value).textReport(tags)
        Clipboard.getSystemClipboard().putString(report)
        Notifications.create()?.text("Report copied to clipboard, size: ${report.length}")?.show()
    }

    fun selectWholeRow() {
        val selectionModel: TableView.TableViewSelectionModel<ScannedSingleRow> = tableView.selectionModel
        val selectedCells: ObservableList<TablePosition<Any, Any>> = selectionModel.selectedCells
        val selectedByRow: Map<Int, List<TablePosition<Any, Any>>> = selectedCells
                .filter { it != null }
                .groupBy { it.row }

        selectedByRow.forEach { entry ->
            @Suppress("UNCHECKED_CAST")
            val notSelected: List<TableColumn<ScannedSingleRow, *>> = tableView.columns
                    .minus(entry.value.map { it -> it.tableColumn as TableColumn<ScannedSingleRow, *> })
            notSelected
                    .forEach { column -> tableView.selectionModel.select(entry.key, column) }
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
                            .filter { !it.allEntriesEqual || it.occurrencesForTag == 1 }
                            .map { tagStats -> createTableColumn(tagStats) }
                    tableView.columns.setAll(newColumns)
                }
            }
        }

        tableView.skin = MaxPrefWidthSkin(150.0, tableView)

    }

    private fun createTableColumn(tagStats: TagStats): TableColumn<ScannedSingleRow, String?> {
        return TableColumn<ScannedSingleRow, String?>(tagStats.tagName).apply {

            cellValueFactory = Callback<TableColumn.CellDataFeatures<ScannedSingleRow, String?>, ObservableValue<String?>> { features ->
                ReadOnlyStringWrapper(features.value.values[tagStats.tagName] ?: "<null>").readOnlyProperty
            }
            isSortable = false
            cellFormat {
                text = it
                textFill = when {
                    it == tagStats.pivotValue && tagStats.occurrencesForTag != 1 -> Color.LIGHTSLATEGREY.brighter()
                    tagStats.pivotValue == null && it == "<null>" -> Color.LIGHTSLATEGREY.brighter().brighter()
                    else -> Color.BLACK
                }
                alignment = if (tagStats.numbersOnly) Pos.CENTER_RIGHT else Pos.CENTER_LEFT
                paddingRight(10.0)
            }
        }
    }
}

class MaxPrefWidthSkin(val maxPrefWidth: Double, tableView: TableView<ScannedSingleRow>?) : TableViewSkin<ScannedSingleRow>(tableView) {

    override fun resizeColumnToFitContent(tc: TableColumn<ScannedSingleRow, *>?, maxRows: Int) {
        super.resizeColumnToFitContent(tc, maxRows)
        if (tc == null) return
        if (tc.width > maxPrefWidth) {
            tc.prefWidth = maxPrefWidth
        }
    }

}

