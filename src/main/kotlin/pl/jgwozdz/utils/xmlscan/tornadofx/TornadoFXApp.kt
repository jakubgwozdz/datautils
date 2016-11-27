package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.beans.property.SimpleObjectProperty
import javafx.stage.Stage
import pl.jgwozdz.utils.xmlscan.AppConfig
import pl.jgwozdz.utils.xmlscan.PropertiesFile
import tornadofx.*
import java.nio.file.Paths

/**
 *
 */

class AppConfigWrapper(file: PropertiesFile, appConfig: AppConfig) {
    val fileProperty = SimpleObjectProperty<PropertiesFile>(file)
    val file by fileProperty
    val configProperty = SimpleObjectProperty<AppConfig>(appConfig)
    var config by configProperty
}

class AppPropertiesWrapperModel : ItemViewModel<AppConfigWrapper>() {
    val file = bind { item?.fileProperty }

    val appConfig = bind { item?.configProperty }
            .apply { onChange { commit() } }
}

class TornadoFXApp : App(MainWindowView::class) {

    private val appPropertiesWrapperModel: AppPropertiesWrapperModel by inject()

//    private val propertiesFile = SimpleObjectProperty<PropertiesFile>()

    private fun configFromParams(parameters: Parameters?): String {
        return parameters?.raw
                ?.filter { it.startsWith("--config=") }
                ?.map { it.removePrefix("--config=") }
                ?.firstOrNull()
                ?: "xmlscan.properties"
    }

    override fun start(stage: Stage) {
        val propertiesFile = PropertiesFile(Paths.get(configFromParams(parameters)))
        appPropertiesWrapperModel.file.value = propertiesFile

        val properties = propertiesFile.readConfig()
        appPropertiesWrapperModel.appConfig.value = properties

        setUserAgentStylesheet(STYLESHEET_MODENA)
        super.start(stage)
    }

    override fun stop() {
        with(appPropertiesWrapperModel) {
//            if (isDirty) rollback()
            file.value.writeConfig(appConfig.value)
        }
    }
}