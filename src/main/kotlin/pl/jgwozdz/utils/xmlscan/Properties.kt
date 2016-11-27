package pl.jgwozdz.utils.xmlscan

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 *
 */

data class AppConfig(var xmlScanConfig: XMLScanConfig, var initialConfig : InitialConfig)

data class InitialConfig(var directory: String)
data class XMLScanConfig(var entryNameXPath: String, var entryDataFromNameXPath: String)







internal val ENTRY_NAME_XPATH = "xmlscan.entryname.xpath"
internal val ENTRY_DATA_FROM_NAME_XPATH = "xmlscan.entrydatafromname.xpath"
internal val INITIAL_DIRECTORY = "initial.directory.path"

class PropertiesFile(val path: Path = Paths.get("xmlscan.properties")) {

    private val defaults = Properties().apply {
        this[ENTRY_NAME_XPATH] = "//Entry/Name"
        this[ENTRY_DATA_FROM_NAME_XPATH] = "./../Records/Record"
        this[INITIAL_DIRECTORY] = "."
    }

    fun readConfig(): AppConfig {
        println("Reading config from '$path'")
        val properties = Properties(defaults).apply {
            try {
                Files.newInputStream(path).use {
                    load(it)
                }
            } catch (e: IOException) {
                println("Cannot read '$path': $e")
            }
        }

        val entryNameXPath = properties.getProperty(ENTRY_NAME_XPATH)
        val entryDataFromNameXPath = properties.getProperty(ENTRY_DATA_FROM_NAME_XPATH)
        val initialDirectory = properties.getProperty(INITIAL_DIRECTORY)

        val xmlScanProperties = XMLScanConfig(entryNameXPath, entryDataFromNameXPath)
        val initialConfig = InitialConfig(initialDirectory)
        return AppConfig(xmlScanProperties, initialConfig)
    }

    fun writeConfig(appConfig: AppConfig) {
        println("Writing config to '$path'")
        val properties = Properties(defaults)
        properties.setProperty(ENTRY_NAME_XPATH, appConfig.xmlScanConfig.entryNameXPath)
        properties.setProperty(ENTRY_DATA_FROM_NAME_XPATH, appConfig.xmlScanConfig.entryDataFromNameXPath)
        properties.setProperty(INITIAL_DIRECTORY, appConfig.initialConfig.directory)
        try {
            Files.newOutputStream(path).use {
                properties.store(it, "XML Scanner data")
            }
        } catch (e: IOException) {
            println("Cannot write '$path': $e")
        }

    }



}
