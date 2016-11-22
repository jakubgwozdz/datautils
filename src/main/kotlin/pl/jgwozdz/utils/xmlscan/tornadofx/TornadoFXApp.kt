package pl.jgwozdz.utils.xmlscan.tornadofx

import tornadofx.App
import tornadofx.find
import java.nio.file.Paths

/**
 *
 */

//fun main(args: Array<String>) {
//    LauncherImpl.launchApplication(TornadoFXApp::class.java, args)
//}
//
class TornadoFXApp : App(MainWindowView::class) {
    init {
        find(FileChooserView::class).model.data.dirToScan = Paths.get("C:\\Users\\gwozd_000\\Downloads")
    }
}