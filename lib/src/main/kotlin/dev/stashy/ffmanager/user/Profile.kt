package dev.stashy.ffmanager.user

import net.harawata.appdirs.AppDirs
import net.harawata.appdirs.AppDirsFactory
import java.io.File
import java.io.FilenameFilter
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class Profile(val root: Path) {
    init {
        require(isProfile(root)) { "Provided path is not a profile." }
    }

    val prefs: Prefs by lazy { Prefs(this) }

    companion object {
        fun getAll(path: Path): List<Profile> {
            val profiles: List<Profile> = mutableListOf()
            return Files.list(path).map {
                Profile(path)
            }.collect(Collectors.toList())
        }

        val default: Profile by lazy {
            val profiles = Path.of(AppDirsFactory.getInstance().getUserDataDir("Firefox", null, "Mozilla", true)).resolve("Profiles")
            val path = profiles.toFile().listFiles { file: File, _: String -> file.isDirectory }?.filter {
                it.name.contains("default-release")
            }?.reduce { _, _ -> throw IllegalStateException("Unable to find default profile.") }?.toPath()
            Profile(path!!)
        }

        fun isProfile(path: Path): Boolean {
            return Files.isDirectory(path) && Files.exists(path.resolve("prefs.js"))
        }
    }
}