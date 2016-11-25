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

fun reportBlockEntry() {
    val stackTrace = Exception().stackTrace
    if (stackTrace.size < 2) return
    val stElem = stackTrace[1]
    var className = stElem.className
    if (className.contains(".")) className = className.substring(className.lastIndexOf(".")+1)

    println("entering $className:${stElem.methodName}")
}