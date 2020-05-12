package dev.stashy.ffmanager.user

import org.junit.Test
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertTrue

class PreferencesTest {
    var prefsPath: Path = Paths.get(Thread.currentThread().contextClassLoader.getResource("profile/prefs.js")!!.toURI())

    @Test
    fun testRead() {
        val prefs = Preferences(prefsPath)
        assertTrue(prefs.containsKey("string") && prefs["string"]!! == "value")
        assertTrue(prefs.containsKey("number") && prefs["number"]!! == "1")
        assertTrue(prefs.containsKey("boolean") && prefs["boolean"]!! == "true")
    }

    @Test
    fun testWrite() {
        val file = File.createTempFile("userChrome", "css")
        file.deleteOnExit()
        val prefs = Preferences(file.toPath())
        val testMap = mapOf(Pair("test1", "value1"), Pair("test2", "value2"))
        testMap.forEach { prefs[it.key] = it.value }
        prefs.flush()
        prefs.read()
        testMap.forEach {
            assertTrue(prefs.containsKey(it.key) && prefs[it.key] == it.value)
        }
    }
}