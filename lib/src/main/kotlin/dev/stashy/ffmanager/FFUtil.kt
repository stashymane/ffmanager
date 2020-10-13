package dev.stashy.ffmanager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object FFUtil {
    fun copyAll(src: Path, dest: Path) {
        Files.walk(src)
            .forEach { sourcePath: Path ->
                val targetPath: Path = dest.resolve(src.relativize(sourcePath))
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }
    }

    fun deleteAll(src: Path) {
        Files.walk(src)
            .sorted(Comparator.reverseOrder())
            .map { it.toFile() }
            .forEach { it.delete() }
    }
}