package pl.jgwozdz.utils.xmlscan.tornadofx

import pl.jgwozdz.utils.xmlscan.XMLScanner
import tornadofx.App

/**
 *
 */

//fun main(args: Array<String>) {
//    LauncherImpl.launchApplication(TornadoFXApp::class.java, args)
//}
//
class TornadoFXApp : App(MainWindowView::class) {


    var xmlScanner: XMLScanner? = null

    val fileToScanModel: FileToScanModel by inject()
    val entryChooserController: EntryChooserController by inject()

    init {


        fileToScanModel.itemProperty.addListener { field, oldVal, newVal: FileToScan? ->
            xmlScanner?.close()
            xmlScanner = newVal?.path?.let { XMLScanner(it) }
            xmlScanner?.run {
                val allEntries = getAllEntries().map(::EntryToAnalyze)
                entryChooserController.entries.setAll(allEntries)
            }
        }

    }
}