package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import java.nio.file.Path

class UserChromeCss(var path: Path) {
    val regex = Regex("@import [\"'](.*)[\"'];")

    constructor(chrome: Chrome) {
        path = chrome.path.resolve("userChrome.css")
    }

    val activePackagePaths = mutableListOf<Path>()

    fun add(pkg: ChromePackage) {

    }

    fun remove(pkg: ChromePackage) {

    }
}