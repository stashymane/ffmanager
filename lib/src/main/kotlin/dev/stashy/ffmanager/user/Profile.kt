package dev.stashy.ffmanager.user

import net.harawata.appdirs.AppDirsFactory
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.streams.asSequence

class Profile(val root: Path) {
    init {
        require(isProfile(root)) { "Provided path is not a profile." }
    }

    val prefs: Prefs by lazy { Prefs(this) }
    val chrome: Chrome by lazy { Chrome(this) }

    companion object {
        fun getAll(path: Path): List<Profile> {
            return Files.list(path).asSequence().mapNotNull {
                try {
                    Profile(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }.toList()
        }

        fun getAll(): List<Profile> {
            return getAll(
                Path.of(AppDirsFactory.getInstance().getUserDataDir("Firefox", null, "Mozilla", true))
                    .resolve("Profiles")
            )
        }

        val default: Profile by lazy {
            getAll().find { it.root.toString().endsWith("default-release") }
                ?: throw NoSuchFileException("Default profile not found.")
        }

        fun isProfile(path: Path): Boolean {
            return Files.isDirectory(path) && Files.exists(path.resolve("prefs.js"))
        }
    }
}