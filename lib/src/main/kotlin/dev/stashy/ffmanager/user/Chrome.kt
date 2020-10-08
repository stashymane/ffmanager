package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.packages.FFPack
import dev.stashy.ffmanager.packages.PackFiles
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class Chrome(val path: Path) :
    ArrayList<FFPack>() {
    constructor(profile: Profile) : this(profile.path.resolve("meta.json"))

    val styles = ChromeStyles()
    val scripts = ChromeScripts()

    init {
        if (!Files.exists(path))
            Files.createDirectory(path)
        reload()
    }

    override fun add(pack: FFPack): Boolean {
        if (contains(pack))
            throw AlreadyInstalledException()
        if (pack !is PackFiles)
            return false
        val ipath = path.resolve(pack.id)
        Files.copy(pack.path, ipath)
        val installed = FFPack.from(ipath)
        enable(installed)
        return super.add(installed)
    }

    override fun remove(pack: FFPack): Boolean {
        if (!contains(pack)) return false
        if (pack !is PackFiles)
            return false
        disable(pack)
        Files.delete(pack.path)
        return super.remove(pack)
    }

    fun enable(pack: FFPack) {

    }

    fun disable(pack: FFPack) {

    }

    fun reload() {
        clear()
        addAll(Files.list(path).filter { Files.isDirectory(it) }.map {
            FFPack.from(it)
        }.toList())
    }
}

class AlreadyInstalledException : Exception()