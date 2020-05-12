package dev.stashy.ffmanager.user

import java.lang.Double.parseDouble
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class Preferences(private val path: Path) : HashMap<String, Any>() {
    constructor(profile: Profile) : this(profile.root.resolve("user.js"))

    companion object {
        private val comments =
            listOf("/*", "  This file is managed by FFManager.", "  Any changes here will be overwritten.", "*/")
    }

    private val regex = Regex("^(?:user_pref\\((?:\"?(.+?)\"?), (?:\"?(.+?)\"?)\\);)")

    init {
        read()
    }

    fun read() {
        clear()
        this["toolkit.legacyUserProfileCustomizations.stylesheets"] = true
        if (Files.exists(path))
            Files.lines(path).forEach {
                val m = regex.matchEntire(it)
                if (m != null && m.groups.count() == 3) {
                    this[m.groupValues[1]] = m.groupValues[2]
                }
            }
    }

    fun flush() {
        Files.write(path, comments.union(map { parseUserPref(it.key, it.value.toString()) }), StandardOpenOption.CREATE)
    }

    private fun parseUserPref(key: String, value: String): String {
        var parsedValue = value
        var isNum = true
        try {
            parseDouble(value)
        } catch (e: NumberFormatException) {
            isNum = false
        }
        if (!isNum || !(value == "true" || value == "false"))
            parsedValue = "\"" + value + "\""
        return "user_pref(\"$key\", $parsedValue);"
    }
}