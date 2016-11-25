package pl.jgwozdz.utils.xmlscan.tornadofx

import org.w3c.dom.Element
import pl.jgwozdz.utils.xmlscan.ScannedData
import pl.jgwozdz.utils.xmlscan.XMLScanner
import tornadofx.App
import java.nio.file.Path
import java.nio.file.Paths

/**
 *
 */

class TornadoFXApp : App(MainWindowView::class) {


    var xmlScanner: XMLScanner? = null

//    val entryToAnalyzeModel: EntryToAnalyzeModel by inject()
    val entryChooserController: EntryChooserController by inject()
    val fileChooserController: FileChooserController by inject()
    val analyzedEntryController: AnalyzedEntryController by inject()

    init {

        // actual logic

        fileChooserController.selectedFile.addListener { field, oldVal, newVal: Path? ->
            xmlScanner?.close()
            xmlScanner = newVal?.let { XMLScanner(it) }
            xmlScanner?.run {
                val allEntries = getAllEntries()
                entryChooserController.entries.setAll(allEntries)
            }
        }

        entryChooserController.selectedEntry.addListener { field, oldVal, newVal: Element? ->
            xmlScanner?.run {
                val scannedData = newVal?.let { getData(it) } ?: ScannedData(listOf())
                analyzedEntryController.scannedData.value = scannedData
            }

        }

        fileChooserController.directoryToScan.path = Paths.get("C:\\Users\\gwozd_000\\Downloads")

    }
}