package pl.jgwozdz.utils.xmlscan.tornadofx.view

import javafx.geometry.Pos
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import org.w3c.dom.Element
import tornadofx.*

class EntryChooserView : View() {

    var entriesList: ListView<Element>? = null

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
            scrollpane {
                isFitToHeight = true
                isFitToWidth = true
                vboxConstraints { vGrow = Priority.ALWAYS }
                entriesList = listview()
            }
        }

    }

}