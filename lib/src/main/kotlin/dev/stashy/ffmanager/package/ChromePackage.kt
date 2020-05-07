package dev.stashy.ffmanager.`package`

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import net.lingala.zip4j.ZipFile
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

data class ChromePackage(
    val id: String,
    @Json(ignored = true)
    var path: Path? = null,
    val name: String? = null,
    val description: String? = null,
    val version: String? = null,
    val compatible: List<String> = listOf(),
    val prefs: Map<String, Any> = mapOf(),
    val updateUrl: URL? = null
) {

    companion object {
        fun from(path: Path): ChromePackage {
            return from(path.toFile())
        }

        fun from(dir: File): ChromePackage {
            require(dir.isDirectory) { "Package path must be a directory." }
            var p = Klaxon().parse<ChromePackage>(dir.resolve("meta.json")) //TODO converter for URL
            if (p != null)
                p.path = dir.toPath()
            else
                p = ChromePackage(dir.name, dir.toPath())
            return p
        }

        fun fromZip(path: Path): ChromePackage? { //TODO read directly from zip
            require(path.endsWith(".zip")) { "Given path is not a zip file." }
            val tempPath = Path.of(System.getProperty("java.io.tmpdir")).resolve(path.fileName)

            ZipFile(path.toFile()).extractAll(tempPath.toString())
            tempPath.toFile().deleteOnExit()
            return from(tempPath)
        }
    }

    override fun equals(other: Any?)
            = (other is ChromePackage)
            && id == other.id
}