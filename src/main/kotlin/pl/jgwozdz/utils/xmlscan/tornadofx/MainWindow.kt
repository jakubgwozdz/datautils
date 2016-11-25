package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.WritableValue
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos.TOP_RIGHT
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.text.TextAlignment.RIGHT
import org.w3c.dom.Element
import pl.jgwozdz.utils.version.VersionLogic
import pl.jgwozdz.utils.xmlscan.ScannedData
import pl.jgwozdz.utils.xmlscan.XMLScanner
import tornadofx.*
import java.nio.file.Path

/**
 *
 */

class MainWindowController : Controller() {

    val version = SimpleStringProperty()

    // this object will be reused for whole file, so I want to keep it in property
    val xmlScanner = SimpleObjectProperty<XMLScanner>()

    // these controllers will be wired together
    private val entryChooserController: EntryChooserController by inject()
    private val fileChooserController: FileChooserController by inject()
    private val analyzedEntryController: AnalyzedEntryController by inject()

    init {
        reportBlockEntry()
        version.value = VersionLogic().title(name = "XML Scanner", artifactId = "xmlscan")

        // wiring the models
        val selectedFile = fileChooserController.selectedFile
        val listToUpdate = entryChooserController.entries
        selectedFile.addListener { observable, oldValue, newValue -> readFileAndCacheScanner(newValue, listToUpdate, xmlScanner) }

        val selectedEntry = entryChooserController.selectedEntry
        val dataToUpdate = analyzedEntryController.scannedData
        selectedEntry.addListener { field, oldVal, newVal: Element? ->
            scanEntry(newVal, dataToUpdate, xmlScanner.value)

        }

    }

    private fun scanEntry(entry: Element?, dataToUpdate: SimpleObjectProperty<ScannedData>, xmlScanner: XMLScanner?) {
        dataToUpdate.value = if (xmlScanner == null || entry == null) ScannedData(listOf()) else xmlScanner.getData(entry)
    }

    // TODO: runAsync but reliable
    private fun readFileAndCacheScanner(path: Path?, listToUpdate: ObservableList<Element>, xmlScanner: WritableValue<XMLScanner>) {
//        println("readFileAndCacheScanner called")
//        runAsync {
//            println("readFileAndCacheScanner async start")
        xmlScanner.value?.close()
        xmlScanner.value = path?.let { XMLScanner(it) }
        val result = xmlScanner.value?.run { getAllEntries() } ?: listOf()
//            println("readFileAndCacheScanner async end")
//            result
//        } ui { result ->
//            println("readFileAndCacheScanner ui start")
        listToUpdate.setAll(result)
//            println("readFileAndCacheScanner ui end")
//        }
//        println("readFileAndCacheScanner ended")
    }

}

class MainWindowView : View(title = VersionLogic().title(name = "XML Scanner", artifactId = "xmlscan")) {

    val ctrl: MainWindowController by inject()

    override val root = borderpane {
        prefWidth = 1300.0
        prefHeight = 700.0
    }

    val fileChooserView: FileChooserView by inject()
    val entryChooserView: EntryChooserView by inject()
    val analyzedEntryView: AnalyzedEntryView by inject()

    init {
        reportBlockEntry()
        with(root) {
            center = splitpane {
                setDividerPositions(0.16)
                splitpane {
                    orientation = VERTICAL
                    setDividerPositions(0.5)
                }.apply {
                    this += fileChooserView.root
                    this += entryChooserView.root
                }
            }.apply {
                this += analyzedEntryView.root
            }

            bottom = hbox(spacing = 5.0) {
                label {
                    text = ""
                    hgrow = ALWAYS
                }
                progressbar(initialValue = 0.0) { }
                separator(orientation = VERTICAL)
                label(ctrl.version) {
                    textAlignment = RIGHT
                }
                alignment = TOP_RIGHT
                padding = Insets(2.0)
            }

        }
    }

}

