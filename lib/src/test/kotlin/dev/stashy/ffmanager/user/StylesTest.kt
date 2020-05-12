package dev.stashy.ffmanager.user

import org.junit.Test
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class StylesTest {

    var chromePath: Path = Paths.get(Thread.currentThread().contextClassLoader.getResource("profile/chrome/userChrome.css")!!.toURI())

    @Test
    fun testRead() {
        val chrome = Styles(chromePath)
        chrome.apply {
            assertEquals(2, count())
            assertTrue(containsAll(listOf(Paths.get("package1/test.css"), Paths.get("package2/test.css"))))
        }
    }

    @Test
    fun testWrite() {
        val file = File.createTempFile("userChrome", "css")
        file.deleteOnExit()
        val chrome = Styles(file.toPath())
        val testPaths = listOf("test1", "test2").map { Paths.get(it) }.toList()
        chrome.apply {
            addAll(testPaths)
            flush()
            read()
            assertTrue(containsAll(testPaths))
        }
    }
}