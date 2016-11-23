package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.scene.control.Button
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.Priority.NEVER
import javafx.stage.DirectoryChooser
import tornadofx.*
import tornadofx.property
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileChooserView : View() {

    val model = FileChooserViewModel()

    override val root = anchorpane {
        title = "File Chooser"

        prefWidth = 200.0
        prefHeight = 400.0
        vbox {
            spacing = 5.0
            anchorpaneConstraints { leftAnchor = 5.0; rightAnchor = 5.0; topAnchor = 5.0; bottomAnchor = 5.0 }

            label("Directory to scan: ")
            hbox {
                spacing = 5.0
                vboxConstraints { vGrow = NEVER }
                textfield {
                    hboxConstraints {
                        hGrow = ALWAYS
                    }
                    bind(property = model.dirToScan, converter = PathConverter())
                    validator {
                        when {
                            it.isNullOrBlank() -> error("must be filled")
                            !Files.isDirectory(Paths.get(it)) -> error("Must me a directory")
                            else -> null
                        }
                    }
                }
                button("...") {
                    setOnAction {
                        onSelectDirectoryButton()
                    }
                }
            }
            label("Found files: ")
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vboxConstraints { vGrow = ALWAYS }
                listview(model.files) {
                    bindSelected(property = model.selectedFile)
//                    selectionModelProperty().onChange { model.commit() }
                }
            }
        }
    }

    internal fun Button.onSelectDirectoryButton() {
        DirectoryChooser()         // create dialog
                .apply {           // with some properties
                    title = "Select directory to scan"
                    initialDirectory = (model.backingValue(model.dirToScan) as Path).toFile()
                }
                .showDialog(this.scene.window) // show it
                ?.let {            // and if not cancelled
                    model.dirToScan.value = it.toPath() // set new value
                }
    }
}

/**
 * For interaction with outside world
 */
class FileChooserModel(dirToScan: Path) {

    var dirToScan: Path by property(dirToScan)
    fun dirToScanProperty(): ObjectProperty<Path> = getProperty(FileChooserModel::dirToScan)

    var selectedFile by property<Path>()
    fun selectedFileProperty() = getProperty(FileChooserModel::selectedFile)

}


/**
 * for VMMV, for interaction with View and for presentation logic
 */

class FileChooserViewModel() : ViewModel() {

    private val updateFileListListener = { observableValue: ObservableValue<out Path>?, oldVal: Path?, newVal: Path? -> updateFileList(newVal) }

    var data = FileChooserModel(Paths.get(".").toAbsolutePath().normalize())
        set(value) {
            val dirToScanPropertyOld = data.dirToScanProperty()
            dirToScanPropertyOld.removeListener(updateFileListListener)
            field = value
            val dirToScanProperty = data.dirToScanProperty()
            dirToScanProperty.addListener(updateFileListListener)
            updateFileList(dirToScanProperty.get())
        }

    val dirToScan: Property<Path> = bind { data.dirToScanProperty() }
            .apply { onChange { commit() } }

    val selectedFile = bind(autocommit = true) { data.selectedFileProperty() }

    val files = mutableListOf<Path>().observable()

    init {
//        data.dirToScanProperty().addListener(updateFileListListener)
//        updateFileList(data.dirToScanProperty().get())

    }

    internal fun updateFileList(newValue: Path?) {
        files.clear()
        if (newValue == null) return
        try {
            Files.newDirectoryStream(newValue) { isXmlFile(it) }
                    .map { newValue.relativize(it) }
                    .forEach { files.add(it) }
        } catch (e: Exception) {
            println("unreadable dir '$newValue': $e")
        }

    }

    fun isXmlFile(it: Path) = Files.isRegularFile(it) && it.fileName.toString().toLowerCase().endsWith(".xml")


}

