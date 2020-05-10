package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import java.lang.IllegalArgumentException
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path

class Chrome(var path: Path) {

    constructor(profile: Profile) : this(profile.root.resolve("chrome"))

    private val userChrome: UserChromeCss by lazy { UserChromeCss(this) }

    val installedPackages: List<ChromePackage>
    get() {
        return path.toFile().listFiles()!!.mapNotNull { if (it.isDirectory) ChromePackage.from(it) else null }
    }

    val enabledPackages: List<ChromePackage>
    get() {
        return userChrome.mapNotNull { try { ChromePackage.from(path.resolve(it)) } catch (e: IllegalArgumentException) { null } }
    }

    fun getInstalledPackage(pkg: ChromePackage): ChromePackage {
        return installedPackages.find { it.id == pkg.id }
            ?: throw NoSuchElementException("Package has not been installed.")
    }

    fun enable(pkg: ChromePackage) {
        userChrome.add(getInstalledPackage(pkg).path!!)
        userChrome.flush()
    }

    fun disable(pkg: ChromePackage) {
        userChrome.remove(getInstalledPackage(pkg).path!!)
        userChrome.flush()
    }

    fun install(pkg: ChromePackage) {
        pkg.path.let {
            require(it != null && Files.isDirectory(it)) { "Package path must be a directory." }
            it.toFile().copyRecursively(path.resolve(pkg.id).toFile())
        }
    }

    fun uninstall(pkg: ChromePackage) {
        val installed = getInstalledPackage(pkg)
        Files.delete(installed.path!!)
    }
}