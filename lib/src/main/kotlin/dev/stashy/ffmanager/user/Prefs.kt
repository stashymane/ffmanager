package dev.stashy.ffmanager.user

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.Double.parseDouble
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

class Prefs(private val path: Path) : HashMap<String, Any>() {
    constructor(profile: Profile) : this(profile.root.resolve("prefs.js"))

    private val regex = Regex("^(?:user_pref\\((?:\"?(.+?)\"?), (?:\"?(.+?)\"?)\\);)")

    init {
        require(Files.exists(path))
        read()
    }

    fun read() {
        Files.lines(path).forEach {
            val m = regex.matchEntire(it)
            if (m != null && m.groups.count() == 3) {
                this[m.groupValues[1]] = m.groupValues[2]
            }
        }
    }

    fun flush() {
        val writtenKeys = mutableListOf<String>()
        val result = Files.lines(path).map {
            val m = regex.matchEntire(it)
            if (m != null && containsKey(m.groupValues[1])) {
                writtenKeys.add(m.groupValues[1])
                parseUserPref(m.groupValues[1], this[m.groupValues[2]].toString())
            } else
                it
        }.collect(Collectors.toList())
        this.filter { !writtenKeys.contains(it.key) }.forEach { result.add(parseUserPref(it.key, it.value.toString())) }
        Files.write(path, result) //TODO allow removal
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