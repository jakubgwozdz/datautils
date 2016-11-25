package pl.jgwozdz.utils.xmlscan.tornadofx

import tornadofx.App
import java.nio.file.Paths

/**
 *
 */

class TornadoFXApp : App(MainWindowView::class) {

    val fileChooserController: FileChooserController by inject()

    init {

        reportBlockEntry()

        fileChooserController.directoryToScan.path = Paths.get("C:\\Users\\gwozd_000\\Downloads")

    }

}