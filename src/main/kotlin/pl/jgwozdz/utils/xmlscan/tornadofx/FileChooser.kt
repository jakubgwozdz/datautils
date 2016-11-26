package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.Priority.ALWAYS
import javafx.stage.DirectoryChooser
import javafx.stage.Window
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DirectoryToScan(path: Path) {
    val pathProperty = SimpleObjectProperty<Path>(path)
    var path by pathProperty
}


class FileChooserController : Controller() {

    private val appPropertiesWrapperModel: AppPropertiesWrapperModel by inject()
    val directoryToScan = DirectoryToScan(Paths.get(".").toAbsolutePath().normalize())
    val files: ObservableList<Path> = FXCollections.observableArrayList<Path>()
    val selectedFile = SimpleObjectProperty<Path>()

    fun onSelectDirectoryButton(window: Window?) {
        DirectoryChooser()         // create dialog
                .apply {           // with some properties
                    title = "Select directory to scan"
                    initialDirectory = directoryToScan.path?.toFile()
                }
                .showDialog(window) // show it
                ?.let {            // and if not cancelled
                    directoryToScan.path = it.toPath() // set new value
                }
    }

    init {
        reportBlockEntry()
        val initialConfig = appPropertiesWrapperModel.appConfig.value.initialConfig
        directoryToScan.pathProperty.addListener { observable, oldValue, newValue ->
            println("dirToScan changed from $oldValue to $newValue ")
            initialConfig.directory = newValue.toString()
            updateFileList()
        }
        directoryToScan.path = Paths.get(initialConfig.directory).toAbsolutePath().normalize()
        updateFileList()
    }

    fun updateFileList() {
        files.clear()
        if (directoryToScan.path == null) return
        try {
            Files.newDirectoryStream(directoryToScan.path) { isXmlFile(it) }
                    .forEach {
                        files.add(it)
                    }
        } catch (e: Exception) {
            println("unreadable dir '$directoryToScan.path': $e")
        }

    }

    fun isXmlFile(it: Path) = Files.isRegularFile(it) && it.fileName.toString().toLowerCase().endsWith(".xml")

}

class DirectoryToScanModel : ItemViewModel<DirectoryToScan>() {
    val path = bind { item?.pathProperty }
            .apply { onChange { commit() } }
}

class FileChooserView : View() {

    val ctrl: FileChooserController by inject()

    val dirToScanModel: DirectoryToScanModel by inject()

    override val root = anchorpane {
        title = "File Chooser"

        prefWidth = 200.0
        prefHeight = 400.0
        vbox {
            spacing = 5.0
            allAnchors = 5.0

            label("Directory to scan: ")
            hbox {
                spacing = 5.0
                textfield {
                    hgrow = ALWAYS
                    bind(property = dirToScanModel.path, converter = PathConverter())
                    validator {
                        when {
                            it.isNullOrBlank() -> error("must be filled")
                            !Files.isDirectory(Paths.get(it)) -> error("Must me a directory")
                            else -> null
                        }
                    }
                    setOnAction { ctrl.updateFileList() }
                }
                button("...") {
                    setOnAction {
                        ctrl.onSelectDirectoryButton(this.scene.window)
                    }
                }
            }
            label("Found files: ")
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vgrow = ALWAYS
                listview(ctrl.files) {
                    bindSelected(ctrl.selectedFile)
                }.cellFormat {
                    text = it.fileName.toString()
                }
            }
        }
    }

    init {
        reportBlockEntry()
        dirToScanModel.rebind { item = ctrl.directoryToScan }
    }

}


