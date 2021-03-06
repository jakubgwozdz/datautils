package pl.jgwozdz.utils.xmlscan.console

import pl.jgwozdz.utils.commandline.ArgsParser
import pl.jgwozdz.utils.commandline.Parameters
import pl.jgwozdz.utils.xmlscan.PropertiesFile
import pl.jgwozdz.utils.xmlscan.PropertiesFile.Companion.DEFAULT_CONFIG
import pl.jgwozdz.utils.xmlscan.ScannedDataAnalyzer
import pl.jgwozdz.utils.xmlscan.TextReporter
import pl.jgwozdz.utils.xmlscan.XMLScanner
import java.nio.file.Paths

/**
 *
 */

fun main(args: Array<String>) {
    ConsoleApp(ArgsParser().parse(args)).launch()
}

class ConsoleApp(val cmdLineParams: Parameters) {
    fun launch() {

        // read (or create) configuration file
        val configFile = cmdLineParams.named["config"]?.let { Paths.get(it) } ?: DEFAULT_CONFIG
        val propertiesFile = PropertiesFile(configFile)

        val appConfig = propertiesFile.readConfig()

        // update config with overriding params
        cmdLineParams.named["initialDirectory"]?.let {
            appConfig.initialConfig.directory = it
        }

        cmdLineParams.named["entryNameXPath"]?.let {
            appConfig.xmlScanConfig.entryNameXPath = it
        }

        cmdLineParams.named["entryDataFromNameXPath"]?.let {
            appConfig.xmlScanConfig.entryDataFromNameXPath = it
        }

        val entriesToSearch = (cmdLineParams.named["entry"]?.split(",") ?: listOf()).map(String::trim).filter(String::isNotBlank)

        val columns = (cmdLineParams.named["columns"]?.split(",") ?: listOf()).map(String::trim).filter(String::isNotBlank)

        // inputFile is mandatory in console run
        val file = cmdLineParams.named["inputFile"]?.let { Paths.get(it) }
                ?: error("No --inputFile=<filename> argument")

        // for command line we don't use initialDir from config file
        val resolvedFile = if (file.isAbsolute) file else {
            cmdLineParams.named["initialDirectory"]?.let { Paths.get(it) }?.resolve(file)?.normalize() ?: file
        }

        val xmlScanner = XMLScanner(resolvedFile, appConfig.xmlScanConfig.entryNameXPath, appConfig.xmlScanConfig.entryDataFromNameXPath)

        val foundNodes = if (entriesToSearch.isEmpty()) xmlScanner.getAllEntries().take(1) else xmlScanner.getSelectedEntries(entriesToSearch)

        if (foundNodes.isEmpty()) error("no entry ${if (entriesToSearch.isNotEmpty()) "containing '${entriesToSearch.joinToString(",")}' " else ""}found for xpath '${xmlScanner.allEntriesXPath}'")

        val analyzer = ScannedDataAnalyzer()

        foundNodes.forEach {
            println("file $resolvedFile, entry ${it.textContent}")
            val scannedData = xmlScanner.getData(it)
            val analyzedData = analyzer.analyzeData(scannedData)
            val reporter = TextReporter(analyzedData)
            val tags = if (columns.isNotEmpty()) columns else analyzedData.tagsStats.map { it -> it.tagName }
            val report = reporter.textReport(tags)
            print(report)

        }
    }

}