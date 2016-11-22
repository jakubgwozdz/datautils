package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.util.StringConverter
import java.nio.file.Path
import java.nio.file.Paths

/**
 *
 */

class PathConverter : StringConverter<Path>() {
    override fun toString(path: Path?): String? {
        return path?.toString()
    }

    override fun fromString(string: String?): Path? {
        return string?.let { Paths.get(it) }
    }

}

