package dev.stashy.ffmanager.`package`

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import net.lingala.zip4j.ZipFile
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

data class ChromePackage(
    val id: String,
    @Json(ignored = true)
    var path: Path? = null,
    val name: String? = null,
    val description: String? = null,
    val version: String? = null,
    val compatible: List<String> = listOf(),
    val prefs: Map<String, Any> = mapOf(),
    val updateUrl: String? = null
) {

    init {
        require(idRegex.matches(id)) { "Package ID must be alphanumeric." }
    }

    companion object {
        val idRegex = Regex("^[a-zA-Z0-9]*\$")

        fun from(path: Path): ChromePackage {
            return from(path.toFile())
        }

        fun from(dir: File): ChromePackage {
            require(dir.isDirectory) { "Package path must be a directory." }
            if (!dir.resolve("meta.json").exists())
                return ChromePackage(dir.name, dir.toPath())
            var p = Klaxon().parse<ChromePackage>(dir.resolve("meta.json"))
            if (p != null)
                p.path = dir.toPath()
            else {
                p = if (idRegex.matches(dir.name))
                    ChromePackage(dir.name, dir.toPath())
                else
                    ChromePackage(getRandomString(6))
            }
            return p
        }

        fun fromZip(path: Path): ChromePackage? { //TODO read directly from zip
            require(path.endsWith(".zip")) { "Given path is not a zip file." }
            val tempPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve(path.fileName)

            ZipFile(path.toFile()).extractAll(tempPath.toString())
            tempPath.toFile().deleteOnExit()
            return from(tempPath)
        }

        fun getRandomString(length: Int): String {
            val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            return (1..length)
                .map { charset.random() }
                .joinToString("")
        }
    }

    override fun equals(other: Any?) = (other is ChromePackage)
            && id == other.id
            && version == other.version

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (version?.hashCode() ?: 0)
        return result
    }
}