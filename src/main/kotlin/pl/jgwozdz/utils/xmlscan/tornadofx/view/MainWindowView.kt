package pl.jgwozdz.utils.xmlscan.tornadofx.view

import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import pl.jgwozdz.utils.version.VersionLogic
import tornadofx.*

/**
 *
 */
class MainWindowView : View(title = VersionLogic().title(name = "XML Scanner", artifactId = "xmlscan")) {

    override val root = borderpane {
        prefWidth = 1300.0
        prefHeight = 700.0
    }

    val fileChooserView: FileChooserView by inject()
    val entryChooserView: EntryChooserView by inject()
    val analysisView: AnalysisView by inject()

    init {
        with(root) {
            center = splitpane {
                splitpane {
                }.apply {
                    setDividerPositions(0.5)
                    orientation = Orientation.VERTICAL
                    this += fileChooserView.root
                    this += entryChooserView.root
                }
            }.apply {
                setDividerPositions(0.16)
                this += analysisView.root
            }

            bottom = hbox(spacing = 5.0) {
                label {
                    text = ""
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                }
                progressbar(initialValue = 0.0) { }
                label {
                    text = VersionLogic().title(name = "XML Scanner", artifactId = "xmlscan")
                    textAlignment = TextAlignment.RIGHT
                }
                alignment = Pos.TOP_RIGHT
                padding = Insets(2.0)
            }

        }
    }

}

class AnalysisView : View() {
    override val root = anchorpane()

}

