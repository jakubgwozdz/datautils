package pl.jgwozdz.utils.xmlscan

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 *
 */

data class AppConfig(var xmlScanConfig: XMLScanConfig)

data class XMLScanConfig(var entryNameXPath: String, var entryDataFromNameXPath: String)

val ENTRY_NAME_XPATH = "xmlscan.entryname.xpath"
val ENTRY_DATA_FROM_NAME_XPATH = "xmlscan.entrydatafromname.xpath"

class PropertiesFile(val path: Path = Paths.get("xmlscan.properties")) {

    val defaults = Properties().apply {
        this[ENTRY_NAME_XPATH] = "//Entry/Name"
        this[ENTRY_DATA_FROM_NAME_XPATH] = "./../Records/Record"
    }

    fun readConfig(): AppConfig {
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
        val xmlScanProperties = XMLScanConfig(entryNameXPath, entryDataFromNameXPath)

        return AppConfig(xmlScanProperties)
    }

    fun writeConfig(appConfig: AppConfig) {
        val properties = Properties(defaults)
        properties.setProperty(ENTRY_NAME_XPATH, appConfig.xmlScanConfig.entryNameXPath)
        properties.setProperty(ENTRY_DATA_FROM_NAME_XPATH, appConfig.xmlScanConfig.entryDataFromNameXPath)
        try {
            Files.newOutputStream(path).use {
                properties.store(it, "XML Scanner data")
            }
        } catch (e: IOException) {
            println("Cannot write '$path': $e")
        }

    }



}
