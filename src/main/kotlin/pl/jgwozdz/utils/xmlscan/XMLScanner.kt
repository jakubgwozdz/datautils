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
class XMLScanner(pathToXml: Path) : Closeable {
    override fun close() {
        inputStream.close()
    }

    private val inputStream = Files.newInputStream(pathToXml)
    private val inputSource = InputSource(inputStream)
    private val xPathFactory = XPathFactory.newInstance()

    //language=XPath2
    var allEntriesXPath = "*/Acct/CSRec/CSAcctNum"

    fun getAllEntries() : List<Element> {
        val entriesExpression = xPathFactory.newXPath().compile(allEntriesXPath)
        val nodeList = entriesExpression.evaluate(inputSource, XPathConstants.NODESET) as NodeList
        return nodeListAsListOfElements(nodeList)
    }

    fun nodeListAsListOfElements(details: NodeList) = (0..details.length - 1)
            .map { details.item(it) }
            .filter { it is Element }
            .map { it as Element }

}