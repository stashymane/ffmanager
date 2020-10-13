package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.FFUtil
import dev.stashy.ffmanager.packages.FFPack
import dev.stashy.ffmanager.packages.PackFiles
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence

class Chrome(val path: Path) {
    constructor(profile: Profile) : this(profile.path.resolve("chrome"))

    init {
        if (!Files.exists(path))
            Files.createDirectory(path)
    }

    val styles = ChromeStyles(this)
    val scripts = ChromeScripts(this)

    val installed
        get() = Files.list(path).asSequence().mapNotNull { FFPack.from(it) }

    fun isInstalled(pack: FFPack): Boolean {
        val p = Paths.get(pack.id)
        return Files.list(path).anyMatch { it.fileName == p }
    }

    fun getInstalled(id: String): Path? {
        val p = Paths.get(id)
        return Files.list(path).asSequence().find { it.fileName == p }
    }

    fun getInstalled(pack: FFPack): Path? {
        return getInstalled(pack.id)
    }

    fun install(pack: PackFiles) {
        if (isInstalled(pack))
            throw AlreadyInstalledException(pack)
        FFUtil.copyAll(pack.path, path.resolve(pack.id))
    }

    fun uninstall(id: String) {
        val p = Paths.get(id)
        Files.list(path).asSequence().find { it.fileName == p }?.let {
            FFUtil.deleteAll(it)
        }
    }

    fun uninstall(pack: FFPack) {
        uninstall(pack.id)
    }

}

class AlreadyInstalledException(pkg: FFPack) : Exception("Package ${pkg.id} has already been installed.")