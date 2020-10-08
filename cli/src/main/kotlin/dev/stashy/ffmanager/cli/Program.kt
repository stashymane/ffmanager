package dev.stashy.ffmanager.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path

fun main(args: Array<String>) {
    Program().main(args)
}

class Program : CliktCommand() {
    val install by option("-i", "--install", help = "Installs selected packages").path(
        mustExist = true,
        mustBeReadable = true
    ).multiple()
    val uninstall by option("-u", "--uninstall", help = "Uninstalls selected packages").multiple()

    val enable by option("-e", "--enable", help = "Enables selected packages").multiple()
    val disable by option("-d", "--disable", help = "Disables selected packages").multiple()

    val list by option("-l", "--list", help = "Lists installed packages").flag()
    val about by option("-a", "--about", help = "Shows more information about selected packages").multiple()

    val profilePath by option(
        "-p",
        "--profile",
        help = "Profile folder name"
    ).path(
        mustExist = true,
        mustBeReadable = true,
        mustBeWritable = true,
        canBeDir = true,
        canBeFile = false
    )

    override fun run() {
        val profile = if (profilePath == null) Profile.default else Profile(profilePath!!)
        if (list) {
            profile.installedPackages.forEach { println(it.id) }
        }
        about.forEach { id ->
            val p = profile.installedPackages.find { pkg -> pkg.id == id }
            p?.apply {
                println("ID: " + this.id)
                println("Installed: true")
                this.name?.let { println("Name: $it") }
                this.description?.let { println("Description: $it") }
                this.version?.let { println("Version: $it") }
                if (this.compatible != null && this.compatible!!.isNotEmpty()) println("Compatible with: " + this.compatible?.joinToString { ", " })
            }
        }

        install.forEach {
            profile.install(LocalPackage.from(it))
        }
        uninstall.forEach { id ->
            profile.findInstalledPackage(id).let {
                profile.uninstall(it)
            }
        }

        enable.forEach { id ->
            profile.findInstalledPackage(id).let {
                profile.enable(it)
            }
        }
        disable.forEach { id ->
            profile.findInstalledPackage(id).let {
                profile.disable(it)
            }
        }
    }

}