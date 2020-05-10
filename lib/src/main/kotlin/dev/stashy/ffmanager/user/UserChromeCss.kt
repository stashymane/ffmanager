package dev.stashy.ffmanager.user

import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

class UserChromeCss(var path: Path) : ArrayList<Path>() {
    constructor(chrome: Chrome) : this(chrome.path.resolve("userChrome.css"))

    val regex = Regex("@import [\"'](.*)[\"'];", RegexOption.IGNORE_CASE)

    init {
        read()
    }

    override fun add(element: Path): Boolean {
        return super.add(relativize(element))
    }

    override fun remove(element: Path): Boolean {
        return super.remove(relativize(element))
    }

    fun read() {
        clear()
        if (Files.exists(path))
            addAll(Files.lines(path).asSequence()
                .map { regex.matchEntire(it) }
                .filter { it != null && it.groups.count() == 2 }
                .map { Path.of(it!!.groups[1]!!.value) })
    }

    fun flush() {
        if (!Files.exists(path))
            Files.createFile(path)
        Files.write(
            path,
            map {
                val relative = if (it.isAbsolute) relativize(it) else it
                "@import \"$relative\";"
            }
        )
    }

    private fun relativize(path: Path) : Path {
        return if (path.isAbsolute) this.path.parent.relativize(path) else path
    }
}