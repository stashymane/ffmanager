package dev.stashy.ffmanager.user

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

        fun isProfile(path: Path): Boolean {
            return Files.isDirectory(path) && Files.exists(path.resolve("prefs.js"))
        }
    }
}