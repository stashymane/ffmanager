package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import java.nio.file.Files

class Chrome(val profile: Profile) {
    val installedPackages = mutableListOf<ChromePackage>()
    val path = profile.root.resolve("chrome")

    fun install(pkg: ChromePackage) {
        Files.copy(pkg.path, path.resolve("/" + pkg.path.fileName))
    }

    fun uninstall(pkg: ChromePackage) {
        Files.delete(pkg.path)
    }
}