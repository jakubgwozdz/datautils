package pl.jgwozdz.utils.xmlscan

import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern
import java.util.stream.Collectors
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Created by jgw on 17/11/2016.
 */

fun main(args: Array<String>) {
    val pathToXml = Paths.get("d:\\data\\16.4\\full Statement Prep XML Sample file  sysprin 8220 2000 QT0Y region.xml")
    val pathToFiles = Paths.get("d:\\data\\BBXML_20161110_1332")

    val xPathFactory = XPathFactory.newInstance()

    fun xpathForEntries() = xPathFactory.newXPath().compile("*/Acct/CSRec/CSAcctNum")!!

    val foundEntries: List<String> = Files.newInputStream(pathToXml).use {
        val entries = xpathForEntries().evaluate(InputSource(it), XPathConstants.NODESET) as NodeList
        elementsList(entries).map { it.textContent }
    }
    println("entries in new file: " + foundEntries.size)

    val entryInFilename = Pattern.compile(".*(\\d{16}).*")

    val allFiles: List<Path> = Files.walk(pathToFiles)
            .collect(Collectors.toList())
            .map { it as Path }

    val foundFiles = allFiles
            .map { it to it.fileName.toString() }
            .map { it.first to entryInFilename.matcher(it.second) }
            .filter { it.second.matches() }
            .map { it.first to it.second.group(1) }
            .toMap()

    println("old files: " + foundFiles.size)

    val matching = foundFiles.filter { foundEntries.contains(it.value) }
    val filesWithoutNewEntries = foundFiles.filter { !foundEntries.contains(it.value) }
    val entriesWithoutOldFiles = foundEntries.filter { !foundFiles.containsValue(it) }

    println("   old files with entries in new file: " + matching.size)
    println("old files without entries in new file: " + filesWithoutNewEntries.size)
    println("entries in new file without old files: " + entriesWithoutOldFiles.size)

    filesWithoutNewEntries.forEach {
        println("deleting ${it.key}")
        Files.delete(it.key)
    }
}