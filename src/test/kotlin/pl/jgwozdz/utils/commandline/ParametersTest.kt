package pl.jgwozdz.utils.commandline

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.expect

/**
 *
 */

class ArgsParserSpec : Spek({
    describe("An ArgsParser") {
        val argsParser = ArgsParser()
        it("should parse empty command") {
            val params = argsParser.parse(arrayOf())
            expect(0) { params.named.size }
            expect(0) { params.unnamed.size }
        }
        it("should parse named param") {
            val params = argsParser.parse(arrayOf("--name=value"))
            expect(1) { params.named.size }
            expect("value") { params.named["name"] }
            expect(0) { params.unnamed.size }
        }
        it("should parse unnamed param") {
            val params = argsParser.parse(arrayOf("value"))
            expect(0) { params.named.size }
            expect(1) { params.unnamed.size }
            expect("value") { params.unnamed[0] }
        }
        it("should parse mixed params") {
            val params = argsParser.parse(arrayOf("unnamed value", "--named=named value", "--another=123", "last unnamed"))
            expect(2) { params.named.size }
            expect(2) { params.unnamed.size }
            expect("unnamed value") { params.unnamed[0] }
            expect("last unnamed") { params.unnamed[1] }
            expect("named value") { params.named["named"] }
            expect("123") { params.named["another"] }
        }
    }


})