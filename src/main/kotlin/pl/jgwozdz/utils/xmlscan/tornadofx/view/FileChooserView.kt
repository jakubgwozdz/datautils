package pl.jgwozdz.utils.xmlscan.tornadofx.view

import javafx.geometry.Pos
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import tornadofx.*
import java.nio.file.Path

class FileChooserView : View() {

    var dirField: TextField by singleAssign()
    var fileList: ListView<Path> by singleAssign()

    override val root = anchorpane {
        prefWidth = 200.0
        vbox {
            alignment = Pos.CENTER
            spacing = 5.0
            anchorpaneConstraints {
                leftAnchor = 5.0
                rightAnchor = 5.0
                topAnchor = 5.0
                bottomAnchor = 5.0
            }
            hbox {
                vboxConstraints { vGrow = Priority.NEVER }
                dirField = textfield {
                    hboxConstraints { hGrow = Priority.ALWAYS }
                }
            }
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vboxConstraints { vGrow = Priority.ALWAYS }
                fileList = listview()
            }
        }
    }

}