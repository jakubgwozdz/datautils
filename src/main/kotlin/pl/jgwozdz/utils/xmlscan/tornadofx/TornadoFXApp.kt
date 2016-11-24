package pl.jgwozdz.utils.xmlscan.tornadofx

import pl.jgwozdz.utils.xmlscan.XMLScanner
import tornadofx.App
import tornadofx.find
import tornadofx.rebind

/**
 *
 */

//fun main(args: Array<String>) {
//    LauncherImpl.launchApplication(TornadoFXApp::class.java, args)
//}
//
class TornadoFXApp : App(MainWindowView::class) {


    val entryChooserData = EntryChooserModel().apply {
        entriesProperty.addListener { observableValue, oldVal, newVal -> println("entries changed from '$oldVal' to '$newVal'") }
        selectedEntryProperty.addListener { observableValue, oldVal, newVal -> println("selectedEntry changed from '$oldVal' to '$newVal'") }
    }

    var xmlScanner: XMLScanner? = null

    val fileToScanModel: FileToScanModel by inject()

    init {

//        find(FileChooserView::class).model.rebind { data = fileChooserData }
        find(EntryChooserView::class).model.rebind { data = entryChooserData }

        fileToScanModel.itemProperty.addListener { field, oldVal, newVal: FileToScan? ->
            xmlScanner?.close()
            xmlScanner = newVal?.path?.let { XMLScanner(it) }
            xmlScanner?.run {
                val allEntries = getAllEntries()
                entryChooserData.entries.setAll(allEntries)
            }
        }

    }
}