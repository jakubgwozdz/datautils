package pl.jgwozdz.utils.xmlscan

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue
import kotlin.test.expect

/**
 *
 */

class TextReporterSpec : Spek({
    describe("An XML Reporter") {
        val textReporter = TextReporter(
                AnalyzedData(// two rows, three columns
                        ScannedData(
                                listOf(
                                        ScannedSingleRow(mapOf("item" to "keyboard", "quantity" to "5", "amount" to "250.00")),
                                        ScannedSingleRow(mapOf("item" to "mouse", "quantity" to "3", "amount" to "180.00"))
                                )
                        ),
                        listOf(
                                TagStats("item", null, false, 8, false, 2),
                                TagStats("quantity", null, false, 8, true, 2),
                                TagStats("amount", null, false, 8, true, 2)
                        )
                )
        )

        it("should return 4 lines on report ended with a newline") {
            val textReport = textReporter.textReport(listOf("item", "quantity", "amount"))
            expect(4) { textReport.trim().lines().size }
            expect("") { textReport.lines().last() }
        }

        it("should report lines of equal length") {
            val textReport = textReporter.textReport(listOf("item", "quantity", "amount"))
            assertTrue { textReport.lines().map { it.length }.filter { it > 0 }.distinct().size == 1 }
        }

        it("should have a horizontal ruler as second line") {
            assertTrue { textReporter.textReport(listOf("item", "quantity", "amount")).lines()[1].matches(Regex("[^\\w\\d]+")) }
        }

        it("should contain specified columns") {
            val header = textReporter.textReport(listOf("item", "amount")).lines()[0]
            assertTrue { header.contains("item") }
            assertTrue { header.contains("amount") }
        }

        it("should not contain not specified columns") {
            val header = textReporter.textReport(listOf("item", "amount")).lines()[0]
            assertTrue { !header.contains("quantity") }
        }

        it("first data row should contain data from first row") {
            val row1 = textReporter.textReport(listOf("item", "amount")).lines()[2]
            assertTrue { row1.contains("keyboard") }
            assertTrue { row1.contains("250.00") }
        }

        it("second data row should contain data from second row") {
            val row2 = textReporter.textReport(listOf("item", "amount")).lines()[3]
            assertTrue { row2.contains("mouse") }
            assertTrue { row2.contains("180.00") }
        }


    }

})