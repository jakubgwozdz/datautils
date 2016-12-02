package pl.jgwozdz.utils.xmlscan.launcher

import kotlin.system.exitProcess

/**
 * Main entry point for application.
 *
 */

fun main(args: Array<String>) {
    try {
        if (args.contains("-c"))
            pl.jgwozdz.utils.xmlscan.console.main(args)
        else
            pl.jgwozdz.utils.xmlscan.tornadofx.main(args)
    } catch (e: NullPointerException) {
        System.err.println("Look! An NPE! How lame!")
        e.printStackTrace()
    } catch (e: Exception) {
        System.err.println(e.message)
        usage()
        if (args.contains("-debug")) e.printStackTrace()
        exitProcess(-1)
    }
}

fun usage() {
    // TODO
}
