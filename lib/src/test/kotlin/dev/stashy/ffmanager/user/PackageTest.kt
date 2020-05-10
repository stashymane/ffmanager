package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals

class PackageTest {
    var testPackage: File = File(Thread.currentThread().contextClassLoader.getResource("package")!!.toURI())
    var testProfile: File = File(Thread.currentThread().contextClassLoader.getResource("profile")!!.toURI())
    var tempPackage: File = Files.createTempDirectory("package").toFile()
    var tempProfile: File = Files.createTempDirectory("profile").toFile()

    lateinit var pkg: ChromePackage
    lateinit var profile: Profile

    @Before
    fun prepare() {
        tempPackage.deleteRecursively()
        tempProfile.deleteRecursively()

        testPackage.copyRecursively(tempPackage)
        testProfile.copyRecursively(tempProfile)

        pkg = ChromePackage.from(tempPackage)
        profile = Profile(tempProfile.toPath())
    }

    @Test
    fun testRead() {
        assertEquals("template", pkg.id)
        assertEquals("Package template", pkg.name)
        assertEquals("A package to be used as a template for building your own.", pkg.description)
        assertEquals("1.0.0", pkg.version)
        assert(pkg.compatible.contains("76") && pkg.compatible.contains("75"))
        assertEquals(mapOf("toolkit.legacyUserProfileCustomizations.stylesheets" to true), pkg.prefs)
        assertEquals("https://github.com/stashymane/ffmanager-package-template", pkg.updateUrl)
    }

    @Test
    fun testInstall() {
        profile.chrome.install(pkg)
        assert(profile.chrome.installedPackages.contains(pkg))
    }

    @Test
    fun testEnable() {
        profile.chrome.install(pkg)
        profile.chrome.enable(pkg)
        assert(profile.chrome.enabledPackages.contains(pkg))
    }
}