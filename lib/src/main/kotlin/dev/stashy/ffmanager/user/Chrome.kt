package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import java.nio.file.Files
import java.nio.file.Path

class Chrome(var path: Path) {

    constructor(profile: Profile) : this(profile.root.resolve("chrome"))

    val userChrome: UserChromeCss by lazy { UserChromeCss(this) }

    private val installedPackages = mutableListOf<ChromePackage>()

    fun enable(pkg: ChromePackage) {

    }

    fun disable(pkg: ChromePackage) {

    }

    fun install(pkg: ChromePackage) {
        pkg.path.let {
            require(it != null && Files.isDirectory(it)) { "Package path must be a directory." }
            Files.copy(it, path.resolve(it.fileName))
        }
    }

    fun uninstall(pkg: ChromePackage) {
        installedPackages.find { it == pkg }.let { Files.delete(it?.path!!) }
    }
}