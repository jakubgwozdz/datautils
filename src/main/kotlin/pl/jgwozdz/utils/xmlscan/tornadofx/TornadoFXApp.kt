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

class AppConfigWrapper(appConfig: AppConfig) {
    val appConfigProperty = SimpleObjectProperty<AppConfig>(appConfig)
    var appConfig by appConfigProperty
}

class AppPropertiesWrapperModel : ItemViewModel<AppConfigWrapper>() {
    val appConfig = bind { item?.appConfigProperty }
            .apply { onChange { commit() } }
}

class TornadoFXApp : App(MainWindowView::class) {

    private val fileChooserController: FileChooserController by inject()
    private val appPropertiesWrapperModel: AppPropertiesWrapperModel by inject()

    val propertiesFile = SimpleObjectProperty<PropertiesFile>()

    private fun configFromParams(parameters: Parameters?): String {
        return parameters?.raw
                ?.filter { it.startsWith("--config=") }
                ?.map { it.removePrefix("--config=") }
                ?.firstOrNull()
                ?: "xmlscan.properties"
    }

    override fun start(stage: Stage) {
        propertiesFile.value = PropertiesFile(Paths.get(configFromParams(parameters)))

        val properties = propertiesFile.value.readConfig()
        appPropertiesWrapperModel.appConfig.value = properties

        fileChooserController.directoryToScan.path = Paths.get("C:\\Users\\gwozd_000\\Downloads")
        super.start(stage)
    }

    override fun stop() {
        propertiesFile.value.writeConfig(appPropertiesWrapperModel.appConfig.value)
    }
}