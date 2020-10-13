package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.FFUtil.copyAll
import dev.stashy.ffmanager.packages.FFPack
import dev.stashy.ffmanager.packages.PackFiles
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence


class ChromeTest {
    var pkgTemplate: Path = Paths.get(Thread.currentThread().contextClassLoader.getResource("package")!!.toURI())
    var profTemplate: Path = Paths.get(Thread.currentThread().contextClassLoader.getResource("profile")!!.toURI())
    var pkg: FFPack
    var profile: Profile

    init {
        val tempPkg = Files.createTempDirectory("pkg")
        copyAll(pkgTemplate, tempPkg)
        val tempProfile = Files.createTempDirectory("prof")
        copyAll(profTemplate, tempProfile)
        pkg = FFPack.from(tempPkg)!!
        profile = Profile(tempProfile, "TEST")
    }

    @Test
    fun install() {
        profile.chrome.install(pkg as PackFiles)
        val pkgPath = profile.chrome.path.resolve(pkg.id)
        assertTrue("Package not installed", Files.exists(pkgPath))
        assertFalse("Installed package does not match source",
            Files.walk(pkgPath).asSequence().zip(Files.walk((pkg as PackFiles).path).asSequence())
                .map { it.first == it.second }.any { false })
        assertEquals("template", FFPack.from(pkgPath)!!.id)

        profile.chrome.uninstall(pkg)
        assertFalse("Failed to uninstall package", profile.chrome.isInstalled(pkg))
    }
}