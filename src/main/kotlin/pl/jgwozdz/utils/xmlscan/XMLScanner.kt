package pl.jgwozdz.utils.xmlscan

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 *
 */
class XMLScanner(pathToXml: Path,
        //language=XPath2
                 var allEntriesXPath: String = "//Acct/CSRec/CSAcctNum"
) : Closeable {

    private val inputStream = Files.newInputStream(pathToXml)
    private val inputSource = InputSource(inputStream)
    private val xPathFactory = XPathFactory.newInstance()

    override fun close() {
        inputStream.close()
    }

    // todo: externalize defaults to property file
    // todo: make it configurable,
    //language=XPath2
    var dataXPath = "./../../DflRec"

    fun getAllEntries(): List<Element> {
        val entriesExpression = xPathFactory.newXPath().compile(allEntriesXPath)
        val nodeList = entriesExpression.evaluate(inputSource, XPathConstants.NODESET) as NodeList
        return nodeListAsListOfElements(nodeList)
    }

    fun getData(entry: Element): ScannedData {
        val mainEntryExpression = xPathFactory.newXPath().compile(dataXPath)

        // Following steps could be done in one long line but I wouldn't understand it tomorrow

        // find all records for entry
        val recordsList = mainEntryExpression.evaluate(entry, XPathConstants.NODESET) as NodeList

        // remap from NodeList to kotlin collections
        val listOfListOfRecordsValues: List<List<Element>> = nodeListAsListOfElements(recordsList)
                .map {
                    nodeListAsListOfElements(it.childNodes) // take all data for each record
                }

        // remap to list of map<Tag, Value> (each map one record)
        val listOfMaps: List<Map<String, String>> = listOfListOfRecordsValues.map {
            it.associateBy({ it.tagName }, { it.textContent })
        }

        // remap to our model and return
        return listOfMaps
                .map {
                    it.let(::ScannedSingleRow)
                }
                .let(::ScannedData)
    }


    fun nodeListAsListOfElements(details: NodeList): List<Element> = (0..details.length - 1)
            .map { details.item(it) }
            .filterIsInstance(Element::class.java)

}

data class ScannedData(val rows: List<ScannedSingleRow>) {

}

data class ScannedSingleRow(val values: Map<String, String>) {

}