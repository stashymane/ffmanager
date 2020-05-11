package dev.stashy.ffmanager.user

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

class StyleManager(var path: Path) : ArrayList<Path>() {
    constructor(chrome: Chrome) : this(chrome.path.resolve("userChrome.css"))

    val regex = Regex("@import [\"'](.*)[\"'];", RegexOption.IGNORE_CASE)

    companion object {
        private val comments = listOf("/*", "  This file is managed by FFManager.", "  Any changes here will be overwritten.", "*/")
    }

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
            addAll(Files.lines(path)
                .map { regex.matchEntire(it) }
                .filter { it != null && it.groups.count() == 2 }
                .map { Paths.get(it!!.groups[1]!!.value) }.toList()
            )
    }

    fun flush() {
        if (!Files.exists(path))
            Files.createFile(path)
        Files.write(
            path,
            comments.union(
                map {
                    val relative = if (it.isAbsolute) relativize(it) else it
                    "@import \"$relative\";"
                })
        )
    }

    private fun relativize(path: Path): Path {
        return if (path.isAbsolute) this.path.parent.relativize(path) else path
    }

    //TODO impl addall & etc. with relativizing
}