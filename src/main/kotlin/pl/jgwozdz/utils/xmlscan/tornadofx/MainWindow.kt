package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.WritableValue
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos.TOP_RIGHT
import javafx.scene.Scene
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.text.TextAlignment.RIGHT
import org.controlsfx.glyphfont.FontAwesome.Glyph.*
import org.controlsfx.glyphfont.GlyphFont
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.w3c.dom.Node
import pl.jgwozdz.utils.version.VersionLogic
import pl.jgwozdz.utils.xmlscan.*
import tornadofx.*
import java.lang.reflect.Method
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
    private val appConfigWrapperModel: AppConfigWrapperModel by inject()

    init {
        reportBlockEntry()
        version.value = VersionLogic().title(name = "XML Scanner", artifactId = "xmlscan")

        // wiring the models
        fileChooserController.selectedFile.addListener { observable, oldValue, newValue -> reScanFile() }
//        appConfigWrapperModel.item.configProperty.addListener { observable, oldValue, newValue -> reScanFile() }

        val selectedEntry = entryChooserController.selectedEntry
        val dataToUpdate = analyzedEntryController.analyzedData
        selectedEntry.addListener { field, oldVal, newVal: Node? ->
            scanEntry(newVal, dataToUpdate, xmlScanner.value)
        }


    }

    fun reScanFile() {
        reportBlockEntry()
        Platform.runLater {
            val selectedFile = fileChooserController.selectedFile
            val listToUpdate = entryChooserController.entries
            readFileAndCacheScanner(selectedFile.value, listToUpdate, xmlScanner)
        }
    }

    private fun scanEntry(entry: Node?, dataToUpdate: SimpleObjectProperty<AnalyzedData>, xmlScanner: XMLScanner?) {
        val scannedData: ScannedData = if (xmlScanner == null || entry == null) ScannedData(listOf()) else xmlScanner.getData(entry)
        dataToUpdate.value = ScannedDataAnalyzer().analyzeData(scannedData)
    }

    // TODO: runAsync but reliable
    private fun readFileAndCacheScanner(path: Path?, listToUpdate: ObservableList<Node>, xmlScanner: WritableValue<XMLScanner>) {
//        println("readFileAndCacheScanner called")
//        runAsync {
//            println("readFileAndCacheScanner async start")
        val start = Instant.now()
        println("scanning $path")
        xmlScanner.value?.close()
        val xmlScanConfig = appConfigWrapperModel.appConfig.value.xmlScanConfig
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
        println("found ${result.size} entries for xpath '${xmlScanConfig.entryNameXPath}', after ${Duration.between(start, end)}")
    }

    fun onConfigurationButton() {

        val dialog = AppConfigDialog(appConfigWrapperModel)

        val result: Optional<AppConfig> = dialog.showAndWait()
        if (result.isPresent) {
            appConfigWrapperModel.appConfig.value = result.get()
            appConfigWrapperModel.commit { reScanFile() }
        }

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
                    button(text = "", graphic = fontAwesome?.create(EYE)) {
                        tooltip("Scenic view if available")
                        setOnAction {
//                            val urlClassLoader = URLClassLoader(arrayOf(Paths.get("scenicView.jar").toUri().toURL()), javaClass.classLoader)
                            val clazz: Class<*>? = Class.forName("org.scenicview.ScenicView")
                            val method: Method? = clazz?.getMethod("show", Scene::class.java)
                            method?.invoke(null, scene)
                        }
//                        isDisable = !Files.isRegularFile(Paths.get("scenicView.jar"))
                        isDisable = try {
                            Class.forName("org.scenicview.ScenicView")
                            false
                        } catch (ex: Exception) {
                            println("no ScenicView found: $ex")
                            true
                        }
                    }
                    button(text = "", graphic = fontAwesome?.create(COLUMNS)) {
                        tooltip("Configure columns (resets on each entry change") { }
                        isDisable = true
                    }
                    button(text = "", graphic = fontAwesome?.create(GEAR)) {
                        tooltip("Configuration") { }
                        setOnAction { ctrl.onConfigurationButton() }
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

