package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.WritableValue
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos.TOP_RIGHT
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment.RIGHT
import org.controlsfx.glyphfont.FontAwesome.Glyph.*
import org.controlsfx.glyphfont.GlyphFont
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.w3c.dom.Element
import pl.jgwozdz.utils.version.VersionLogic
import pl.jgwozdz.utils.xmlscan.*
import tornadofx.*
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.*

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
    private val appPropertiesWrapperModel: AppPropertiesWrapperModel by inject()

    init {
        reportBlockEntry()
        version.value = VersionLogic().title(name = "XML Scanner", artifactId = "xmlscan")

        // wiring the models
        val selectedFile = fileChooserController.selectedFile
        val listToUpdate = entryChooserController.entries
        selectedFile.addListener { observable, oldValue, newValue -> readFileAndCacheScanner(newValue, listToUpdate, xmlScanner) }

        val selectedEntry = entryChooserController.selectedEntry
        val dataToUpdate = analyzedEntryController.analyzedData
        selectedEntry.addListener { field, oldVal, newVal: Element? ->
            scanEntry(newVal, dataToUpdate, xmlScanner.value)

        }

    }

    private fun scanEntry(entry: Element?, dataToUpdate: SimpleObjectProperty<AnalyzedData>, xmlScanner: XMLScanner?) {
        val scannedData: ScannedData = if (xmlScanner == null || entry == null) ScannedData(listOf()) else xmlScanner.getData(entry)
        dataToUpdate.value = ScannedDataAnalyzer().analyzeData(scannedData)
    }

    // TODO: runAsync but reliable
    private fun readFileAndCacheScanner(path: Path?, listToUpdate: ObservableList<Element>, xmlScanner: WritableValue<XMLScanner>) {
//        println("readFileAndCacheScanner called")
//        runAsync {
//            println("readFileAndCacheScanner async start")
        val start = Instant.now()
        println("scanning $path")
        xmlScanner.value?.close()
        val xmlScanConfig = appPropertiesWrapperModel.appConfig.value.xmlScanConfig
        xmlScanner.value = path?.let { XMLScanner(it, xmlScanConfig.entryNameXPath, xmlScanConfig.entryDataFromNameXPath) }
        val result = xmlScanner.value?.run { getAllEntries() } ?: listOf()
//            println("readFileAndCacheScanner async end")
//            result
//        } ui { result ->
//            println("readFileAndCacheScanner ui start")
        listToUpdate.setAll(result)
//            println("readFileAndCacheScanner ui end")
//        }
//        println("readFileAndCacheScanner ended")
        val end = Instant.now()
        println("found ${result.size} entries after ${Duration.between(start, end)}")
    }

    val fontAwesome: GlyphFont? = GlyphFontRegistry.font("FontAwesome")

    fun onConfigurationButton(event: ActionEvent?) {

        val appConfig = appPropertiesWrapperModel.appConfig.value
        val xmlScanConfig = appConfig.xmlScanConfig

        val entryXPath = SimpleStringProperty(xmlScanConfig.entryNameXPath)
        val dataXPath = SimpleStringProperty(xmlScanConfig.entryDataFromNameXPath)

        val dialog = Dialog<AppConfig>().apply {
            title = "Configuration"
            headerText = "Setting are written to ${appPropertiesWrapperModel.file.value.path} during application exit"
            graphic = fontAwesome?.create(GEAR)
            with(dialogPane) {
                content = Form()
                with(content) {
                    fieldset("XML Scanner configuration", fontAwesome?.create(CODE)?.color(Color.DARKRED)) {
                        field("XPath to the entry label") {
                            textfield {
                                prefWidth = 200.0
                            }.bind(entryXPath)
                        }
                        field("XPath to the data, relative to entry label") {
                            textfield {
                                prefWidth = 200.0
                            }.bind(dataXPath)
                        }
                    }
                }
                buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
            }

            setResultConverter { dialogButton ->
                if (dialogButton?.buttonData == OK_DONE) appConfig.copy().apply {
                    xmlScanConfig.entryNameXPath = entryXPath.value
                    xmlScanConfig.entryDataFromNameXPath = dataXPath.value
                } else null
            }

        }

        val result: Optional<AppConfig>? = dialog.showAndWait()

    }

}

class MainWindowView : View(title = VersionLogic().title(name = "XML Scanner", artifactId = "xmlscan")) {

    val ctrl: MainWindowController by inject()
    val fontAwesome: GlyphFont? = GlyphFontRegistry.font("FontAwesome")

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
            top = toolbar {
                hbox(spacing = 5.0) {
                    alignment = TOP_RIGHT
                    hgrow = ALWAYS
                    button(text = "", graphic = fontAwesome?.create(COLUMNS)) {
                        tooltip("Configure columns (resets on each entry change") { }
                    }
                    button(text = "", graphic = fontAwesome?.create(GEAR)) {
                        tooltip("Configuration") { }
                        setOnAction { ctrl.onConfigurationButton(it) }
                    }
                }
            }

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

