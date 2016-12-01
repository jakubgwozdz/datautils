package pl.jgwozdz.utils.xmlscan.tornadofx

import javafx.application.Application
import javafx.stage.Stage
import pl.jgwozdz.utils.commandline.Parameters
import pl.jgwozdz.utils.xmlscan.PropertiesFile
import tornadofx.App
import java.nio.file.Paths

/**
 *
 */

fun main(args: Array<String>) {
    Application.launch(TornadoFXApp::class.java, *args)
}

class TornadoFXApp : App(MainWindowView::class) {

    private val appConfigWrapperModel: AppConfigWrapperModel by inject()

    override fun start(stage: Stage) {

        val cmdLineParams = Parameters(parameters?.named?: mapOf(),
                parameters?.unnamed?: listOf())

        val propertiesFile = PropertiesFile(Paths.get(cmdLineParams.named["config"]?:"xmlscan.properties"))
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