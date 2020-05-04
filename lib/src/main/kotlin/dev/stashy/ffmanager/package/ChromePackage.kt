package dev.stashy.ffmanager.`package`

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.github.zafarkhaja.semver.Version
import net.lingala.zip4j.ZipFile
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

data class ChromePackage(
    val id: String,
    @Json(ignored = true)
    var path: Path? = null,
    val name: String? = null,
    val description: String? = null,
    val version: Version? = null,
    val compatible: List<Version> = listOf(),
    val updateUrl: URL? = null
) {

    companion object {
        fun fromPath(path: Path): ChromePackage {
            require(Files.isDirectory(path)) { "Package path must be a directory." }
            var p = Klaxon().parse<ChromePackage>(path.resolve("meta.json").toFile())
            if (p != null)
                p.path = path
            else
                p = ChromePackage(path.fileName.toString(), path)
            return p
        }

        fun fromZip(path: Path): ChromePackage? {
            require(path.endsWith(".zip")) { "Given path is not a zip file." }
            val tempPath = Path.of(System.getProperty("java.io.tmpdir")).resolve(path.fileName)

            ZipFile(path.toFile()).extractAll(tempPath.toString())
            tempPath.toFile().deleteOnExit()
            return fromPath(tempPath)
        }
    }

    override fun equals(other: Any?)
            = (other is ChromePackage)
            && id == other.id
    // ...
}