package pl.jgwozdz.utils.commandline

/**
 *
 */

val namedRegex = Regex("--(\\w+)=(.*)")

class ArgsParser() {
    fun parse(args: Array<String>): Parameters {
        val named = mutableMapOf<String, String>()
        val unnamed = mutableListOf<String>()

        args.forEach {
            val match = namedRegex.matchEntire(it)
            if (match != null) named += match.groupValues[1] to match.groupValues[2]
            else unnamed += it
        }

        return Parameters(named, unnamed)
    }
}

data class Parameters(val named: Map<String, String>, val unnamed: List<String>)