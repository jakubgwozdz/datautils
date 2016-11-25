package pl.jgwozdz.utils.xmlscan

import java.util.*

/**
 *
 */

class ScannedDataAnalyzer {

    fun analyzeData (scannedData: ScannedData): AnalyzedData {

        // find all distinct tags and create statistics builders for them
        val distinctTags = scannedData.rows
                .flatMap { it.values.keys }
                .distinct() // only distinct tagNames
                .map { it to TagStatsBuilder(it) }
                .toMap()

        // populate these builders with data
        scannedData.rows
                .flatMap { it.values.entries }
                .forEach {
                    val tagName = it.key
                    val value = it.value
                    distinctTags[tagName]?.append(value)
                }

        // calculate statistics and build object
        val tagsStats: List<TagStats> = distinctTags.map { it.value.build(scannedData.rows.size) }

        return AnalyzedData(scannedData, tagsStats)
    }

}

internal class TagStatsBuilder(val tagName: String) {

    val existingValues = HashMap<String, Int>()
    var pivotValue: String? = null
    var allEntriesEqual = false
    var maxLength = tagName.length
    var numbersOnly = false

    fun append(value: String) : TagStatsBuilder {
        existingValues[value] = (existingValues[value] ?: 0) + 1
//        pivotValue = pivotValue ?: value
        if (maxLength < value.length) maxLength = value.length
        return this
    }

    fun build(totalRows: Int) : TagStats {
        val occurrencesForTag = existingValues.values.sum()
        allEntriesEqual = (existingValues.size == 1 && occurrencesForTag == totalRows)
        numbersOnly = existingValues.all { it.key.trim().matches(Regex("^-?\\d*\\.?\\d*$")) }
        if (maxLength > 35) maxLength = 35
        if (maxLength < tagName.length) maxLength = tagName.length

        val blanks = existingValues.keys.filter { it.isBlank() }
        val mostOften = existingValues.maxBy { it.value }?.key
        val mostOftenNumber = existingValues.filterKeys { it.all { it == '0' || it == '.' } }.maxBy { it.value }?.key

        pivotValue = when {
            allEntriesEqual -> mostOften
            occurrencesForTag < totalRows -> null
            blanks.size == 1 -> blanks[0]
            mostOften?.all { it == '0' || it == '.' } ?: false -> mostOften
            mostOftenNumber != null && numbersOnly -> mostOftenNumber
            else -> null
        }
        return TagStats(tagName, pivotValue, allEntriesEqual, maxLength, numbersOnly)

    }
}

data class TagStats(val tagName: String, val pivotValue: String?, val allEntriesEqual: Boolean = false, val maxLength: Int, val numbersOnly : Boolean)

data class AnalyzedData(val scannedData: ScannedData, val tagsStats: List<TagStats>)
