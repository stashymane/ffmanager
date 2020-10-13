package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.packages.FFPack
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.streams.toList

class ChromeStyles(val path: Path, val profilePath: Path = path.parent) : ArrayList<FFPack>() {
    constructor(chrome: Chrome) : this(chrome.path.resolve("userChrome.css"), chrome.path)

    companion object {
        val regex = Regex("@import url\\(\"(.+)\"\\);")
    }

    init {
        if (!Files.exists(path))
            Files.createFile(path)
        load()
    }

    //TODO: implement overrides so that ffpacks stay the same after reloading

    fun load() {
        clear()
        addAll(Files.lines(path)
            .map { regex.matchEntire(it) }
            .filter { it != null }
            .map { it!!.groupValues[0] }
            .map { Paths.get(it) }
            .map { it.parent }
            .map { FFPack.from(it)!! }
            .toList()
        )
    }

    fun save() {
        Files.write(
            path,
            this.map { it.id }.map { "@import url(\"./$it/userChrome.css\");" }.toList(),
            StandardOpenOption.TRUNCATE_EXISTING
        )
    }
}