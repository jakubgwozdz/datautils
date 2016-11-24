package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.scene.Node
import javafx.scene.layout.AnchorPane

/**
 *
 */

var Node.allAnchors: Double?
    get() = AnchorPane.getTopAnchor(this)
    set(value) {
        AnchorPane.setLeftAnchor(this, value)
        AnchorPane.setTopAnchor(this, value)
        AnchorPane.setRightAnchor(this, value)
        AnchorPane.setBottomAnchor(this, value)
    }