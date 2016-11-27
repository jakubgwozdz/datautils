package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.paint.Color
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFont
import org.controlsfx.glyphfont.GlyphFontRegistry
import pl.jgwozdz.utils.xmlscan.AppConfig
import pl.jgwozdz.utils.xmlscan.PropertiesFile
import tornadofx.*

/**
 *
 */

class AppConfigWrapper(file: PropertiesFile, appConfig: AppConfig) {
    val fileProperty = SimpleObjectProperty<PropertiesFile>(file)
    val file by fileProperty
    val configProperty = SimpleObjectProperty<AppConfig>(appConfig)
    var config by configProperty
}

class AppConfigWrapperModel : ItemViewModel<AppConfigWrapper>() {
    val file = bind { item?.fileProperty }

    val appConfig = bind { item?.configProperty }
            .apply { onChange { commit() } }
}

class AppConfigDialog(appConfigWrapperModel: AppConfigWrapperModel) : Dialog<AppConfig>() {

    init {
        val fontAwesome: GlyphFont? = GlyphFontRegistry.font("FontAwesome")
        val appConfig = appConfigWrapperModel.appConfig.value
        val entryXPath = SimpleStringProperty(appConfig.xmlScanConfig.entryNameXPath)
        val dataXPath = SimpleStringProperty(appConfig.xmlScanConfig.entryDataFromNameXPath)

        title = "Configuration"
        headerText = "Setting are written to ${appConfigWrapperModel.file.value.path} during application exit"
        graphic = fontAwesome?.create(FontAwesome.Glyph.GEAR)
        with(dialogPane) {
            content = Form()
            with(content) {
                fieldset("XML Scanner configuration", fontAwesome?.create(FontAwesome.Glyph.CODE)?.color(Color.DARKRED)) {
                    field("XPath to the entry label") {
                        textfield(entryXPath) {
                            prefWidth = 200.0
                        }
                    }
                    field("XPath to the data, relative to entry label") {
                        textfield(dataXPath) {
                            prefWidth = 200.0
                        }
                    }
                }
            }
            buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        }

        setResultConverter { dialogButton ->
            if (dialogButton?.buttonData == ButtonBar.ButtonData.OK_DONE) appConfig.copy().apply {
                this.xmlScanConfig.entryNameXPath = entryXPath.value
                this.xmlScanConfig.entryDataFromNameXPath = dataXPath.value
            } else null
        }

    }
}