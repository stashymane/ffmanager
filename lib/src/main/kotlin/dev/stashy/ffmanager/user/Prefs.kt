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

    private val pattern = Pattern.compile("^(?:user_pref\\((?:\"?(.+?)\"?), (?:\"?(.+?)\"?)\\);)")

    init {
        require(Files.exists(path))
        Files.newBufferedReader(path).use {
            var line: String? = it.readLine()
            var m: Matcher
            while (line != null) {
                m = pattern.matcher(line)
                if (m.find())
                    this[m.group(1)] = m.group(2)
                line = it.readLine()
            }
            it.close()
        }
    }

    fun flush() {
        val writtenKeys = mutableListOf<String>()
        val result = Files.lines(path).map { l ->
            val m = pattern.matcher(l)
            if (m.find() && containsKey(m.group(1))) {
                writtenKeys.add(m.group(1))
                parseUserPref(m.group(1), this[m.group(2)].toString())
            } else
                l
        }.collect(Collectors.toList())
        this.filter { !writtenKeys.contains(it.key) }.forEach { result.add(parseUserPref(it.key, it.value.toString())) }
        Files.write(path, result)
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