package pl.jgwozdz.utils.xmlscan.tornadofx

import pl.jgwozdz.utils.xmlscan.XMLScanner
import tornadofx.App
import tornadofx.find
import tornadofx.rebind
import java.nio.file.Paths

/**
 *
 */

//fun main(args: Array<String>) {
//    LauncherImpl.launchApplication(TornadoFXApp::class.java, args)
//}
//
class TornadoFXApp : App(MainWindowView::class) {

    val fileChooserData = FileChooserModel(Paths.get("C:\\Users\\gwozd_000\\Downloads")).apply {
        dirToScanProperty().addListener { observableValue, oldVal, newVal -> println("dirToScan changed from '$oldVal' to '$newVal'") }
        selectedFileProperty().addListener { observableValue, oldVal, newVal -> println("selectedFile changed from '$oldVal' to '$newVal'") }
    }

    val entryChooserData = EntryChooserModel().apply {
        entriesProperty.addListener { observableValue, oldVal, newVal -> println("entries changed from '$oldVal' to '$newVal'") }
        selectedEntryProperty.addListener { observableValue, oldVal, newVal -> println("selectedEntry changed from '$oldVal' to '$newVal'") }
    }

    var xmlScanner: XMLScanner? = null

    init {

        find(FileChooserView::class).model.rebind { data = fileChooserData }
        find(EntryChooserView::class).model.rebind { data = entryChooserData }
        find(AnalysisView::class).fileChooserModel = fileChooserData

        fileChooserData.selectedFileProperty().addListener { observableValue, oldPath, newPath ->
            xmlScanner?.close()
            xmlScanner = XMLScanner(fileChooserData.dirToScan.resolve(newPath)).apply {
                val allEntries = getAllEntries()
                entryChooserData.entries.setAll(allEntries)
            }
        }

    }
}