package pl.jgwozdz.utils.xmlscan.tornadofx

import pl.jgwozdz.utils.xmlscan.ScannedData
import pl.jgwozdz.utils.xmlscan.XMLScanner
import tornadofx.App
import java.nio.file.Paths

/**
 *
 */

class TornadoFXApp : App(MainWindowView::class) {


    var xmlScanner: XMLScanner? = null

    val fileToScanModel: FileToScanModel by inject()
    val entryToAnalyzeModel: EntryToAnalyzeModel by inject()
    val entryChooserController: EntryChooserController by inject()
    val fileChooserController: FileChooserController by inject()
    val analyzedEntryController:AnalyzedEntryController by inject()

    init {

        // actual logic

        fileToScanModel.itemProperty.addListener { field, oldVal, newVal: FileToScan? ->
            xmlScanner?.close()
            xmlScanner = newVal?.path?.let { XMLScanner(it) }
            xmlScanner?.run {
                val allEntries = getAllEntries().map(::EntryToAnalyze)
                entryChooserController.entries.setAll(allEntries)
            }
        }

        entryToAnalyzeModel.itemProperty.addListener { field, oldVal, newVal: EntryToAnalyze? ->
            xmlScanner?.run {
                val scannedData = newVal?.entry?.let { getData(it) } ?: ScannedData(listOf())
//                scannedData.rows.forEach(::println)
                analyzedEntryController.scannedData.value = scannedData
            }

        }

        fileChooserController.directoryToScan.path = Paths.get("C:\\Users\\gwozd_000\\Downloads")

    }
}