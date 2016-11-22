package pl.jgwozdz.utils.version

import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.InputStream
import java.net.JarURLConnection
import java.nio.file.Files
import java.nio.file.Paths
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * finds the version number in maven pom.xml and compilation time from this class
 */


class VersionLogic {

    fun version(groupId: String = "pl.jgwozdz.utils", artifactId: String): String {
        return versionFromPomXml(this.javaClass.getResourceAsStream("/META-INF/maven/$groupId/$artifactId/pom.xml"))
                ?: versionFromPomXml(Files.newInputStream(Paths.get("pom.xml")))
                ?: "unknown"
    }

    val compile = XPathFactory.newInstance()
            .newXPath()
            .compile("/*[local-name()='project']/*[local-name()='version']")!!

    fun versionFromPomXml(inputStream: InputStream?): String? {
        when (inputStream) {
            null -> return null
            else -> try {
                inputStream.use {
                    val inputSource = InputSource(inputStream)
                    return (compile.evaluate(inputSource, XPathConstants.NODE) as Node?)?.textContent
                }
            } catch (e: Exception) {
                println("'$e' when reading version from pom.xml")
                return null
            }
        }
    }

    fun buildDateTime(): String {

        val resource = javaClass.getResource("${javaClass.simpleName}.class")

        val fileTime = when (resource?.protocol) {
            "file" -> {
                Files.getLastModifiedTime(Paths.get(resource.toURI()))
            }
            "jar" -> {
                val jarURLConnection = resource.openConnection() as JarURLConnection
                val lastModifiedTime = jarURLConnection.jarFile.entries().asSequence()
                        .map { it.lastModifiedTime }
                        .max() ?: return "unknown"
                jarURLConnection.inputStream.close()
                lastModifiedTime
            }
            else -> return "unknown"
        }
        return fileTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .let { "${it.toLocalDate()} ${it.toLocalTime().truncatedTo(ChronoUnit.MINUTES)}" }

    }

    fun title(name: String, groupId: String = "pl.jgwozdz.utils", artifactId: String): String {
        return "$name version ${version(groupId, artifactId)} compiled on ${buildDateTime()}"
    }

}