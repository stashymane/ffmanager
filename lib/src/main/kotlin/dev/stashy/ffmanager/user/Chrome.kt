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
    //val scripts = ChromeScripts(this)

    val installed
        get() = Files.list(path).asSequence().mapNotNull { FFPack.from(it) }

    fun isInstalled(pack: FFPack): Boolean {
        val p = Paths.get(pack.id)
        return Files.list(path).anyMatch { it.fileName == p }
    }

    fun getInstalled(id: String): FFPack? {
        val p = Paths.get(id)
        Files.list(path).asSequence().find { it.fileName == p }?.let { return FFPack.from(it) } ?: return null
    }

    fun getInstalled(pack: FFPack): FFPack? {
        return getInstalled(pack.id)
    }

    fun install(pack: PackFiles) {
        if (isInstalled(pack))
            throw AlreadyInstalledException(pack)
        var copyFrom = pack.path
        copyFrom.resolve("chrome").let {
            if (Files.isDirectory(it))
                copyFrom = it
        }
        FFUtil.copyAll(copyFrom, path.resolve(pack.id))
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

    fun enable(pack: FFPack) {
        styles += pack
        styles.save()
    }

    fun disable(pack: FFPack) {
        styles -= pack
        styles.save()
    }
}

class AlreadyInstalledException(pkg: FFPack) : Exception("Package ${pkg.id} has already been installed.")