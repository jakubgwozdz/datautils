package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.Priority.NEVER
import javafx.stage.DirectoryChooser
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileChooserView : View() {

    val model = FileChooserViewModel(FileChooserData(Paths.get(".").toAbsolutePath().normalize()))

    override val root = anchorpane {
        title = "File Chooser"

        prefWidth = 200.0
        prefHeight = 400.0
        vbox {
//            alignment = Pos.CENTER
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
                        DirectoryChooser()
                                .apply {
                                    title = "Select directory to scan"
                                    model.data.dirToScan.let { initialDirectory = it.toFile() }
                                }
                                .showDialog(this.scene.window)
                                ?.let {
                                    model.dirToScan.value = it.toPath()
//                                    model.commit()
                                }
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
}

class FileChooserData(dirToScan: Path, selectedFile: Path? = null) {

    var dirToScan: Path by property(dirToScan)
    fun dirToScanProperty(): ObjectProperty<Path> = getProperty(FileChooserData::dirToScan)

    var selectedFile by property(selectedFile)
    fun selectedFileProperty() = getProperty(FileChooserData::selectedFile)

    init {
        dirToScanProperty().addListener {
            observableValue, oldVal, newVal ->
            println("${(observableValue as Property).name} changed from '$oldVal' to '$newVal'")
        }
        selectedFileProperty().addListener {
            observableValue, oldVal, newVal ->
            println("${(observableValue as Property).name} changed from '$oldVal' to '$newVal'")
        }
    }

}

class FileChooserViewModel(var data: FileChooserData) : ViewModel() {
    val dirToScan = bind { data.dirToScanProperty() }
            .apply {
                onChange {
                    commit()
                }
            }

    val selectedFile = bind(autocommit = true) { data.selectedFileProperty() }

    val files = mutableListOf<Path>().observable()

    init {
        data.dirToScanProperty().addListener { observableValue: ObservableValue<out Path>?, oldVal: Path?, newVal: Path? -> updateFileList(newVal) }
        updateFileList(data.dirToScan)
    }

    internal fun updateFileList(newValue: Path?) {
        files.clear()
        if (newValue == null) return
        try {
            Files.newDirectoryStream(newValue) { isXmlFile(it) }
                    .map { newValue.relativize(it) }
                    .forEach { files.add(it) }
        } catch (e: Exception) {
            println(newValue + " unreadable: " + e)
        }

    }

    fun isXmlFile(it: Path) = Files.isRegularFile(it) && it.fileName.toString().toLowerCase().endsWith(".xml")


}

