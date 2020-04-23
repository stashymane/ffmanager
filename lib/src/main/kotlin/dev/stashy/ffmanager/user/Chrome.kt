package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import java.nio.file.Files
import java.nio.file.Path

class Chrome(var path: Path) {

    constructor(profile: Profile): this(profile.root.resolve("chrome"))

    private val installedPackages = mutableListOf<ChromePackage>()

    fun install(pkg: ChromePackage) {
        val dest = path.resolve(pkg.path.fileName)
        if (pkg.path.parent == Path.of(System.getProperty("java.io.tmpdir")))
            Files.move(pkg.path, dest)
        else
            Files.copy(pkg.path, dest)

    }

    fun uninstall(pkg: ChromePackage) {
        if (installedPackages.contains(pkg)) {
            Files.delete(pkg.path)
        }
    }
}