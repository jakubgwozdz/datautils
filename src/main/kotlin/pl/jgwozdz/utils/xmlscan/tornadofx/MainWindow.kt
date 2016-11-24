package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.geometry.Insets
import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos.TOP_RIGHT
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.text.TextAlignment.RIGHT
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
    val analyzedEntryView: AnalyzedEntryView by inject()

    init {
        with(root) {
            center = splitpane {
                setDividerPositions(0.16)
                splitpane {
                    orientation = VERTICAL
                    setDividerPositions(0.5)
                }.apply {
                    this += fileChooserView.root
                    this += entryChooserView.root
                }
            }.apply {
                this += analyzedEntryView.root
            }

            bottom = hbox(spacing = 5.0) {
                label {
                    text = ""
                    hgrow = ALWAYS
                }
                progressbar(initialValue = 0.0) { }
                separator(orientation = VERTICAL)
                label {
                    text = VersionLogic().title(name = "XML Scanner", artifactId = "xmlscan")
                    textAlignment = RIGHT
                }
                alignment = TOP_RIGHT
                padding = Insets(2.0)
            }

        }
    }

}

