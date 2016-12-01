package pl.jgwozdz.utils.xmlscan.console

import pl.jgwozdz.utils.commandline.ArgsParser
import pl.jgwozdz.utils.commandline.Parameters

/**
 *
 */

fun main(args: Array<String>) {
    ConsoleApp(ArgsParser().parse(args)).launch()
}

class ConsoleApp(val parameters: Parameters) {
    fun launch() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}