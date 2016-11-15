package pl.jgwozdz.datautils

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 *
 */
fun main(args: Array<String>) {
    val pathToXml = Paths.get(System.getProperty("user.home"), "Downloads", "2016_4.xml")
    val acctNo = "8220204121929090"

    XMLReporter().launch(pathToXml, acctNo)

}

class XMLReporter {
    private val xPathFactory = XPathFactory.newInstance()

    fun expressionForAccountNr(acctNo: String) = xPathFactory.newXPath().compile("*/Acct[CSRec/CSAcctNum='$acctNo']")!!
    fun expressionForDetail() = xPathFactory.newXPath().compile("./DflRec")!!

    fun launch(pathToXml: Path, acctNo: String) {
        println("Searching for account $acctNo inside $pathToXml")
        val accountExpr = expressionForAccountNr(acctNo)
        Files.newInputStream(pathToXml).use {
            val inputSource = InputSource(it)

            val invoices = accountExpr.evaluate(inputSource, XPathConstants.NODESET)
            if (invoices is NodeList) {
                println("Found ${invoices.length} invoice(s)")

                val detailExpr = expressionForDetail()
                (0..invoices.length - 1)
                        .map { invoices.item(it) }
                        .map { detailExpr.evaluate(it, XPathConstants.NODESET) }
                        .forEach { analyze(it as NodeList) }
            }
        }
    }

    fun analyze(details: NodeList) {
        println("Found ${details.length} detail(s)")
        (0..details.length - 1)
                .map { details.item(it) as Element }
                .map { analyzeRecord(it)}
                .forEach { println(it) }

    }

    private fun analyzeRecord(it: Element): Map<String, String> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
