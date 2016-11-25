package pl.jgwozdz.utils.xmlscan

import org.apache.commons.lang3.StringUtils
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.Closeable
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

    val xmlReporter = XMLReporter(pathToXml)
    val entries = xmlReporter.getAllEntries()
    if (args.size > 1) {
        entries.filter { it.textContent == args[1] }.forEach { xmlReporter.getData(it) }
    } else {
        entries.forEach { xmlReporter.getData(it) }
    }
}

fun elementsList(details: NodeList) = (0..details.length - 1)
        .map { details.item(it) }
        .filter { it is Element }
        .map { it as Element }

open class XMLReporter(val pathToXml: Path) : Closeable {

    override fun close() {
        inputStream.close()
    }

    private val inputStream = Files.newInputStream(pathToXml)
    private val inputSource = InputSource(inputStream)

    private val xPathFactory = XPathFactory.newInstance()

    fun expressionForSpecificAccount(acctNo: String) = xPathFactory.newXPath().compile("*/Acct[CSRec/CSAcctNum='$acctNo']")!!
    fun expressionForAllAccounts() = xPathFactory.newXPath().compile("*/Acct")!!
    fun expressionForDetail() = xPathFactory.newXPath().compile("./DflRec")!!
    fun expressionForAccountNr() = xPathFactory.newXPath().compile("./CSRec/CSAcctNum")!!

    fun launchForAcct(acctNo: String) {
        println("Searching for account $acctNo inside $pathToXml")
        val accountExpr = expressionForSpecificAccount(acctNo)
        launchForExpression(accountExpr)
    }

    fun launchForAll() {
        println("Searching for all accounts inside $pathToXml")
        val accountExpr = expressionForAllAccounts()
        launchForExpression(accountExpr)
    }

    fun getAllEntries() : List<Element> {
        val entriesExpression = xPathFactory.newXPath().compile("*/Acct/CSRec/CSAcctNum")
        return elementsList(entriesExpression.evaluate(inputSource, XPathConstants.NODESET) as NodeList)
    }

    fun getData(entry : Element) {
        val mainEntryExpression = xPathFactory.newXPath().compile("./../..")
        analyzeInvoice(mainEntryExpression.evaluate(entry, XPathConstants.NODE) as Element)
    }

    protected fun launchForExpression(accountExpr: XPathExpression) {

        val invoices = accountExpr.evaluate(inputSource, XPathConstants.NODESET)
        if (invoices is NodeList) {
            println("Found ${invoices.length} invoice(s)")

            elementsList(invoices)
                    .forEach { analyzeInvoice(it) }
        }
    }

    val detailExpr = expressionForDetail()
    val accountExpr = expressionForAccountNr()
    fun analyzeInvoice(invoice: Element) {
        println("Analyzing invoice for account ${accountExpr.evaluate(invoice)}")
        analyze(detailExpr.evaluate(invoice, XPathConstants.NODESET) as NodeList)
        println()
    }

    fun analyze(details: NodeList) {
        println("Found ${details.length} detail(s)")
        val parsedDetails = elementsList(details).map { analyzeRecord(it) }

//        parsedDetails.forEach { println(it) }

        // todo: for sure it can be done better with kotlin classes

        val distinctTags: Map<String, TagStatsBuilder> = parsedDetails
                .flatMap { it.keys }
                .distinct()
                .map { it to TagStatsBuilder(it) }
                .toMap()

        parsedDetails
                .flatMap { it.entries }
                .forEach {
                    val tagName = it.key
                    val value = it.value
                    distinctTags[tagName]?.append(value)
                }

        distinctTags.forEach { it.value.build(parsedDetails.size) }

        val allValuesSame = distinctTags.filterValues { it.allEntriesEqual }

        val pivotedTags = distinctTags
                .filterNot { allValuesSame.containsKey(it.key) }
                .filterValues { !it.pivotValue.isNullOrBlank() }

        val tagsToDisplay = distinctTags.filterValues { !it.allEntriesEqual }.map { it.value }

        val headers = tagsToDisplay.map { StringUtils.center(it.tagName, it.maxLength) }
        val ruler = tagsToDisplay.map { StringUtils.leftPad("", it.maxLength, "-") }

        val records = parsedDetails.map { tagValueMap ->
            tagsToDisplay.map { tag ->
                tagValueMap[tag.tagName]
                        .let {
                            if (it == null || it == tag.pivotValue) "" else
                                if (it.startsWith(" ") || it.endsWith(" ")) "'$it'"
                                else StringUtils.abbreviate(it, tag.maxLength)
                        }
                        .let {
                            if (tag.numbersOnly) StringUtils.leftPad(it, tag.maxLength)
                            else StringUtils.rightPad(it, tag.maxLength)
                        }
            }
        }

        println(headers)
        println(ruler)
        records.forEach(::println)

        println("all values same for specified tags: ${allValuesSame.map { "${it.key}='${it.value.pivotValue}'" }.joinToString("; ")}")

        println("empty cells mean default values: ${pivotedTags.map { "${it.key}='${it.value.pivotValue}'" }.joinToString("; ")}")

    }

    private fun analyzeRecord(record: Element): Map<String, String> {
        return elementsList(record.childNodes).associateBy({ it -> it.tagName }, { it -> it.textContent })
    }
}

