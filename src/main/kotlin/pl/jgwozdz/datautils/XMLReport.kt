package pl.jgwozdz.datautils

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

/**
 *
 */
fun main(args: Array<String>) {

    if (args.isEmpty()) {
        println("Please provide a filename as a command-line argument")
        return
    }
    val pathToXml = Paths.get(args[0])

    if (args.size > 1) {
        XMLReporter().launchForAcct(pathToXml, args[1])
    } else {
        XMLReporter().launchForAll(pathToXml)
    }
}

fun elementsList(details: NodeList) = (0..details.length - 1)
        .map { details.item(it) }
        .filter { it is Element }
        .map { it as Element }

open class XMLReporter {
    private val xPathFactory = XPathFactory.newInstance()

    fun expressionForSpecificAccount(acctNo: String) = xPathFactory.newXPath().compile("*/Acct[CSRec/CSAcctNum='$acctNo']")!!
    fun expressionForAllAccounts() = xPathFactory.newXPath().compile("*/Acct")!!
    fun expressionForDetail() = xPathFactory.newXPath().compile("./DflRec")!!
    fun expressionForAccountNr() = xPathFactory.newXPath().compile("./CSRec/CSAcctNum")!!

    fun launchForAcct(pathToXml: Path, acctNo: String) {
        println("Searching for account $acctNo inside $pathToXml")
        val accountExpr = expressionForSpecificAccount(acctNo)
        launchForExpression(pathToXml, accountExpr)
    }

    fun launchForAll(pathToXml: Path) {
        println("Searching for all accounts inside $pathToXml")
        val accountExpr = expressionForAllAccounts()
        launchForExpression(pathToXml, accountExpr)
    }

    protected fun launchForExpression(pathToXml: Path, accountExpr: XPathExpression) {
        Files.newInputStream(pathToXml).use {
            val inputSource = InputSource(it)

            val invoices = accountExpr.evaluate(inputSource, XPathConstants.NODESET)
            if (invoices is NodeList) {
                println("Found ${invoices.length} invoice(s)")

                elementsList(invoices)
                        .forEach { analyzeInvoice(it) }
            }
        }
    }

    val detailExpr = expressionForDetail()
    val accountExpr = expressionForAccountNr()
    fun analyzeInvoice(invoice: Element) {
        println("Analyzing invoice for account ${accountExpr.evaluate(invoice)}")
        analyze(detailExpr.evaluate(invoice, XPathConstants.NODESET) as NodeList)
    }

    fun analyze(details: NodeList) {
        println("Found ${details.length} detail(s)")
        elementsList(details)
                .map { analyzeRecord(it) }
                .forEach { println(it) }
    }

    private fun analyzeRecord(record: Element): Map<String, String> {
        return elementsList(record.childNodes).associateBy({ it -> it.tagName }, { it -> it.textContent })
    }
}
