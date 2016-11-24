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
    val analysisView: AnalysisView by inject()

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
                this += analysisView.root
            }

            bottom = hbox(spacing = 5.0) {
                label {
                    text = ""
                    hboxConstraints {
                        hGrow = ALWAYS
                    }
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

class AnalysisView : View() {
    override val root = anchorpane {
        button("report") {
            setOnAction {
//                val fileChooserViewModel = find(MainWindowView::class).fileChooserView.model
//                println("Dirty: " + fileChooserViewModel.dirToScan)
//                println("Backed: " + fileChooserViewModel.backingValue(fileChooserViewModel.dirToScan))
//                println("FromModel: " + fileChooserModel?.dirToScan)
//                println("selectedFile: " + find(MainWindowView::class).fileChooserView.model.data.selectedFile)
            }
        }
    }

}

