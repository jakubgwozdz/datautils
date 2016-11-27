package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.stage.Stage
import pl.jgwozdz.utils.xmlscan.PropertiesFile
import tornadofx.App
import java.nio.file.Paths

/**
 *
 */

class TornadoFXApp : App(MainWindowView::class) {

    private val appConfigWrapperModel: AppConfigWrapperModel by inject()

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
        appConfigWrapperModel.file.value = propertiesFile

        val properties = propertiesFile.readConfig()
        appConfigWrapperModel.appConfig.value = properties

        setUserAgentStylesheet(STYLESHEET_MODENA)
        super.start(stage)
    }

    override fun stop() {
        with(appConfigWrapperModel) {
//            if (isDirty) rollback()
            file.value.writeConfig(appConfig.value)
        }
    }
}