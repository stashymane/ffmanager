package dev.stashy.ffmanager.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import dev.stashy.ffmanager.packages.FFPack
import dev.stashy.ffmanager.packages.PackFiles
import dev.stashy.ffmanager.packages.PackMeta
import dev.stashy.ffmanager.user.Profile

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
        val profile = if (profilePath == null) Profile.default else Profile(profilePath!!, "profile")
        if (list) {
            profile.chrome.installed.forEach { println(it.id) }
        }
        about.forEach { id ->
            val p = profile.chrome.installed.find { pkg -> pkg.id == id }
            p?.apply {
                println("ID: " + this.id)
                println("Installed: true")
                if (this is PackMeta) {
                    this.name?.let { println("Name: $it") }
                    this.description?.let { println("Description: $it") }
                    this.version?.let { println("Version: $it") }
                    if (this.compatible != null && this.compatible!!.isNotEmpty()) println("Compatible with: " + this.compatible?.joinToString { ", " })
                }
            }
        }

        install.forEach {
            profile.chrome.install(FFPack.from(it) as PackFiles)
        }
        uninstall.forEach { id ->
            profile.chrome.uninstall(id)
        }

        enable.forEach { id ->
            profile.chrome.getInstalled(id)?.enable(profile) ?: println("Unable to find package $id.")
        }
        disable.forEach { id ->
            profile.chrome.getInstalled(id)?.disable(profile) ?: println("Unable to find package $id.")
        }
    }

}