package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

class UserChromeCss(var path: Path) {
    constructor(chrome: Chrome): this(chrome.path.resolve("userChrome.css"))

    val regex = Regex("@import [\"'](.*)[\"'];", RegexOption.IGNORE_CASE)

    init {
        read()
    }

    lateinit var activePackagePaths: MutableList<Path>

    fun add(path: Path) {
        activePackagePaths.add(this.path.relativize(path.parent))
    }

    fun remove(path: Path) {
        activePackagePaths.remove(this.path.relativize(path.parent))
    }

    fun read() {
        activePackagePaths =
            if (Files.exists(path))
                Files.lines(path).asSequence()
                    .map { regex.matchEntire(it) }
                    .filter { it != null && it.groups.count() == 2 }
                    .map { Path.of(it!!.groups[1]!!.value) }
                    .toMutableList()
            else
                mutableListOf()
    }

    fun flush() {
        if (!Files.exists(path))
            Files.createFile(path)
        Files.write(path,
            activePackagePaths.map {
                val relative = if (it.isAbsolute) it.relativize(path.parent) else it
                "@import \"$relative\";"
            }.toList())
    }
}