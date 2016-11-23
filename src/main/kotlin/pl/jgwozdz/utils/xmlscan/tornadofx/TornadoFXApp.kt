package pl.jgwozdz.utils.xmlscan.tornadofx

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
    init {
        val fileChooserData = FileChooserModel(Paths.get("C:\\Users\\gwozd_000\\Downloads"))
        find(FileChooserView::class).model.rebind { data = fileChooserData }
        find(AnalysisView::class).fileChooserModel = fileChooserData
    }
}