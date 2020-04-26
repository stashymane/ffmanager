package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

class UserChromeCss(var path: Path) {
    val regex = Regex("@import [\"'](.*)[\"'];", RegexOption.IGNORE_CASE)

    constructor(chrome: Chrome): this(chrome.path.resolve("userChrome.css"))

    init {
        read()
    }

    lateinit var activePackagePaths: MutableList<Path>

    fun add(pkg: ChromePackage) {
        activePackagePaths.add(pkg.path.relativize(path.parent))
    }

    fun remove(pkg: ChromePackage) {
        activePackagePaths.remove(pkg.path.relativize(path.parent))
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