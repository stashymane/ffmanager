package dev.stashy.ffmanager.`package`

import com.beust.klaxon.Klaxon
import net.lingala.zip4j.ZipFile
import java.nio.file.Files
import java.nio.file.Path

class ChromePackage (
    val path: Path
) {
    var meta: ChromePackageMeta? = null

    init {
        require(Files.isDirectory(path)) { "Package path must be a directory." }
        val metaPath = path.resolve("meta.json")
        meta = Klaxon().parse<ChromePackageMeta>(metaPath.toFile())
    }

    companion object {
        fun fromZip(path: Path): ChromePackage {
            require(path.endsWith(".zip")) { "Given path is not a zip file." }
            val tempPath = Path.of(System.getProperty("java.io.tmpdir") + "/" + path.fileName)

            ZipFile(path.toFile()).extractAll(tempPath.toString())
            return ChromePackage(tempPath)
        }
    }
}

data class ChromePackageMeta(val name: String, val description: String?, val version: String?, val compatible: List<String>?)