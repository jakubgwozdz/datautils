package pl.jgwozdz.datautils

import org.apache.commons.lang3.StringUtils
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
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
        println()
    }

    fun analyze(details: NodeList) {
        println("Found ${details.length} detail(s)")
        val parsedDetails = elementsList(details).map { analyzeRecord(it) }

//        parsedDetails.forEach { println(it) }

        // todo: for sure it can be done better with kotlin classes

        val distinctTags: Map<String, TagStats> = parsedDetails
                .flatMap { it.keys }
                .distinct()
                .map { it to TagStats(it) }
                .toMap()

        parsedDetails
                .flatMap { it.entries }
                .forEach {
                    val tagName = it.key
                    val value = it.value
                    distinctTags[tagName]?.update(value)
                }

        distinctTags.forEach { it.value.finish(parsedDetails.size) }

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
        records.forEach {  println(it) }

        println("all values same for specified tags: ${allValuesSame.map { "${it.key}='${it.value.pivotValue}'" }.joinToString("; ")}")

        println("empty cells mean default values: ${pivotedTags.map { "${it.key}='${it.value.pivotValue}'" }.joinToString("; ")}")

    }

    private fun analyzeRecord(record: Element): Map<String, String> {
        return elementsList(record.childNodes).associateBy({ it -> it.tagName }, { it -> it.textContent })
    }
}

class TagStats(val tagName: String) {

    val existingValues = HashMap<String, Int>()
    var pivotValue: String? = null
    var allEntriesEqual = false
    var maxLength = tagName.length
    var numbersOnly = false

    fun update(value: String) {
        existingValues[value] = (existingValues[value] ?: 0) + 1
//        pivotValue = pivotValue ?: value
        if (maxLength < value.length) maxLength = value.length
    }

    fun finish(totalEntries: Int) {
        val entriesForTag = existingValues.values.sum()
        allEntriesEqual = (existingValues.size == 1 && entriesForTag == totalEntries)
        numbersOnly = existingValues.all { it.key.trim().matches(Regex("^-?\\d*\\.?\\d*$")) }
        if (maxLength > 35) maxLength = 35
        if (maxLength < tagName.length) maxLength = tagName.length

        val blanks = existingValues.keys.filter { it.isBlank() }
        val mostOften = existingValues.maxBy { it.value }?.key
        val mostOftenNumber = existingValues.filterKeys { it.all { it == '0' || it == '.' } }.maxBy { it.value }?.key

        pivotValue = when {
            allEntriesEqual -> mostOften
            entriesForTag < totalEntries -> null
            blanks.size == 1 -> blanks[0]
            mostOften?.all { it == '0' || it == '.' } ?: false -> mostOften
            mostOftenNumber != null && numbersOnly -> mostOftenNumber
            else -> null
        }


    }
}
