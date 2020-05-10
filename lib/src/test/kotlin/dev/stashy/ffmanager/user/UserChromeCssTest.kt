package dev.stashy.ffmanager.user

import org.junit.Test
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class UserChromeCssTest {

    var chromePath: Path = Path.of(Thread.currentThread().contextClassLoader.getResource("profile/chrome/userChrome.css")!!.toURI())

    @Test
    fun testRead() {
        val chrome = UserChromeCss(chromePath)
        chrome.apply {
            assertEquals(2, count())
            assertTrue(containsAll(listOf(Path.of("package1/test.css"), Path.of("package2/test.css"))))
        }
    }

    @Test
    fun testWrite() {
        val file = File.createTempFile("userChrome", "css")
        file.deleteOnExit()
        val chrome = UserChromeCss(file.toPath())
        val testPaths = listOf("test1", "test2").map { Path.of(it) }.toList()
        chrome.apply {
            addAll(testPaths)
            flush()
            read()
            assertTrue(containsAll(testPaths))
        }
    }
}