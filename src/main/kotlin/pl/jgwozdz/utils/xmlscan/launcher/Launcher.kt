package pl.jgwozdz.utils.xmlscan.launcher

/**
 * Main entry point for application.
 *
 */

fun main(args: Array<String>) {

    if (args.contains("-c"))
        pl.jgwozdz.utils.xmlscan.console.main(args)
    else
        pl.jgwozdz.utils.xmlscan.tornadofx.main(args)
}
