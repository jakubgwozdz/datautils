package pl.jgwozdz.utils.xmlscan

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.expect

/**
 *
 */

class TextReporterSpec : Spek({
    describe("An XML Reporter") {
        val textReporter = TextReporter(
                AnalyzedData(
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

        it("should return 4 lines on report") {
            expect(5) { textReporter.textReport(listOf("item", "amount")).trim().lines().size }
        }


    }

})