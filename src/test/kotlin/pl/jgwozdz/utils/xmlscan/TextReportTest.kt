package pl.jgwozdz.utils.xmlscan

import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertTrue
import kotlin.test.expect

/**
 *
 */
@RunWith(JUnitPlatform::class)
class TextReporterSpec : Spek({
    describe("An XML Reporter") {

        val twoByThree = AnalyzedData(// two rows, three columns
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

        val textReporter = TextReporter(twoByThree
        )

        it("should return 4 lines on report ended with a newline") {
            val textReport = textReporter.textReport(listOf("item", "quantity", "amount"))
            expect(4) { textReport.toString().trim().lines().size }
            expect("") { textReport.toString().lines().last() }
        }

        it("should report lines of equal length") {
            val textReport = textReporter.textReport(listOf("item", "quantity", "amount"))
            assertTrue { textReport.toString().lines().map { it.length }.filter { it > 0 }.distinct().size == 1 }
        }

        it("should have the header as second line") {
            val textReport = textReporter.textReport(listOf("item", "quantity", "amount"))
            expect(textReport.header) { textReport.toString().lines()[0] }
        }

        it("should have a horizontal ruler as second line") {
            val textReport = textReporter.textReport(listOf("item", "quantity", "amount"))
            assertTrue { textReport.ruler.matches(Regex("[^\\w\\d]+")) }
            expect(textReport.ruler) { textReport.toString().lines()[1] }
        }

        it("should contain specified columns") {
            val header = textReporter.textReport(listOf("item", "amount")).header
            assertTrue { header.contains("item") }
            assertTrue { header.contains("amount") }
        }

        it("should not contain not specified columns") {
            val header = textReporter.textReport(listOf("item", "amount")).header
            assertTrue { !header.contains("quantity") }
        }

        it("should have rows at third and later lines") {
            val textReport = textReporter.textReport(listOf("item", "amount"))
            val records = textReport.records
            for (i in 0 until records.size) expect(records[i]) { textReport.toString().lines()[i + 2] }
        }


        it("first data row should contain data from first row") {
            val row1 = textReporter.textReport(listOf("item", "amount")).records[0]
            assertTrue { row1.contains("keyboard") }
            assertTrue { row1.contains("250.00") }
        }

        it("second data row should contain data from second row") {
            val row2 = textReporter.textReport(listOf("item", "amount")).records[1]
            assertTrue { row2.contains("mouse") }
            assertTrue { row2.contains("180.00") }
        }

        it("should return columns in specified order") {
            val header = textReporter.textReport(listOf("amount", "item")).header
            header.should.match(Regex(".*amount.*item.*"))
        }

    }

})