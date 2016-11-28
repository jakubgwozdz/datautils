package pl.jgwozdz.utils.xmlscan

import org.apache.commons.lang3.StringUtils

class TextReporter(val analyzedData: AnalyzedData) {

    fun textReport(tags: List<String>): String {

        val tagsToDisplay = analyzedData.tagsStats
                .filter { it.tagName in tags }

        val headers = computeHeader(tagsToDisplay)
        val ruler = computeRuler(tagsToDisplay)

        val records = analyzedData.scannedData.rows.map { tagValueMap ->
            computeRow(tagValueMap, tagsToDisplay)
        }
        val sb = StringBuilder()
        sb.append("$headers\n")
        sb.append("$ruler\n")
        records.forEach{sb.append("$it\n")}
        return sb.toString()
    }

    fun computeRow(row: ScannedSingleRow, tagsToDisplay: List<TagStats>): List<String> {
        return tagsToDisplay.map { tag ->
            row.values[tag.tagName]
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

    fun computeRuler(tagsToDisplay: List<TagStats>) = tagsToDisplay.map { StringUtils.leftPad("", it.maxLength, "-") }

    fun computeHeader(tagsToDisplay: List<TagStats>) = tagsToDisplay.map { StringUtils.center(it.tagName, it.maxLength) }

}

