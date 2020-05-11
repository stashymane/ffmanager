package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import net.harawata.appdirs.AppDirsFactory
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence

class Profile(val root: Path) {
    init {
        require(isProfile(root)) { "Provided path is not a profile." }
    }

    val prefs: Prefs by lazy { Prefs(this) }
    val chrome: Chrome by lazy { Chrome(this) }

    val installedPackages: List<ChromePackage>
        get() {
            return Files.list(chrome.path).asSequence()
                .mapNotNull { if (Files.isDirectory(it)) ChromePackage.from(it) else null }.toList()
        }

    val enabledPackages: List<ChromePackage>
        get() {
            return chrome.enabled
        }

    fun findInstalledPackage(id: String): ChromePackage {
        return installedPackages.find { it.id == id }?: throw IllegalArgumentException("Package $id is not installed.")
    }

    fun enable(pkg: ChromePackage) {
        findInstalledPackage(pkg.id).let {
            chrome.enable(it)
        }
    }

    fun disable(pkg: ChromePackage) {
        findInstalledPackage(pkg.id).let {
            chrome.disable(it)
        }
    }

    fun install(pkg: ChromePackage) {
        pkg.path.let {
            require(it != null && Files.isDirectory(it)) { "Package path must be a directory." }
            it.toFile().copyRecursively(chrome.path.resolve(pkg.id).toFile())
        }
    }

    fun uninstall(pkg: ChromePackage) {
        findInstalledPackage(pkg.id).let {
            chrome.disable(it)
            Files.delete(it.path!!)
        }
    }

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
                Paths.get(AppDirsFactory.getInstance().getUserDataDir("Firefox", null, "Mozilla", true))
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