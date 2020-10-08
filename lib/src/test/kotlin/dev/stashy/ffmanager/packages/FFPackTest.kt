package dev.stashy.ffmanager.packages

import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Paths

class FFPackTest {
    var testPackage = Paths.get(Thread.currentThread().contextClassLoader.getResource("package")!!.toURI())

    @Test
    fun testMeta() {
        val pkg = FFPack.from(testPackage)
        assertTrue(pkg is PackMeta)
        val meta = pkg as PackMeta
        assertEquals("template", pkg.id)
        assertArrayEquals(meta.compatible?.toTypedArray(), arrayOf("76", "75"))
    }
}