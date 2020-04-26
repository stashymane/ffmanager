package dev.stashy.ffmanager.user

import org.junit.Test
import java.io.File
import java.nio.file.Path
import kotlin.test.assertTrue

class PrefsTest {
    var prefsPath: Path = Path.of(Thread.currentThread().contextClassLoader.getResource("profile/prefs.js")!!.toURI())

    @Test
    fun testRead() {
        val prefs = Prefs(prefsPath)
        assertTrue(prefs.containsKey("string") && prefs["string"]!! == "value")
        assertTrue(prefs.containsKey("number") && prefs["number"]!! == "1")
        assertTrue(prefs.containsKey("boolean") && prefs["boolean"]!! == "true")
    }

    @Test
    fun testWrite() {
        val file = File.createTempFile("userChrome", "css")
        file.deleteOnExit()
        val prefs = Prefs(file.toPath())
        val testMap = mapOf(Pair("test1", "value1"), Pair("test2", "value2"))
        testMap.forEach { prefs[it.key] = it.value }
        prefs.flush()
        prefs.read()
        testMap.forEach {
            assertTrue(prefs.containsKey(it.key) && prefs[it.key] == it.value)
        }
    }
}