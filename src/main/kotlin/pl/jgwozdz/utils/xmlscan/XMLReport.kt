package pl.jgwozdz.utils.xmlscan

import org.apache.commons.lang3.StringUtils

class XMLReporter(val analyzedData: AnalyzedData) {

    fun textReport(tags: List<String>): String {

        val tagsToDisplay = analyzedData.tagsStats
                .filter { it.tagName in tags }

        val headers = tagsToDisplay.map { StringUtils.center(it.tagName, it.maxLength) }
        val ruler = tagsToDisplay.map { StringUtils.leftPad("", it.maxLength, "-") }

        val records = analyzedData.scannedData.rows.map { tagValueMap ->
            tagsToDisplay.map { tag ->
                tagValueMap.values[tag.tagName]
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
        val sb = StringBuilder()
        sb.append("$headers\n")
        sb.append("$ruler\n")
        records.forEach{sb.append("$it\n")}
        return sb.toString()
    }

}

