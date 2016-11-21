package pl.jgwozdz.utils.xmlscan.tornadofx.view

import javafx.scene.layout.BorderPane
import pl.jgwozdz.utils.version.VersionLogic
import tornadofx.View

/**
 *
 */
class MainWindowView : View(title = VersionLogic().title(name = "XML Scanner", artifactId = "xmlscan")) {
    override val root = BorderPane()
}