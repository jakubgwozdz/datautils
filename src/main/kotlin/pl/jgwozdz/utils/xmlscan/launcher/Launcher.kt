package pl.jgwozdz.utils.xmlscan.launcher

import pl.jgwozdz.utils.commandline.ArgsParser

/**
 *
 */

fun main(args: Array<String>) {

    println(args.asList())
    val params = ArgsParser().parse(args)

    println("'${params.named["test"]}'")
    println("'${params.named["test2"]}'")

}