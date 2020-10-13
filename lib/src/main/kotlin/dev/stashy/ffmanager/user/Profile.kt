package dev.stashy.ffmanager.user

import net.harawata.appdirs.AppDirsFactory
import org.ini4j.Ini
import org.ini4j.Wini
import java.nio.file.Path
import java.nio.file.Paths

class Profile(val path: Path, val name: String) {
    val chrome = Chrome(this)

    companion object {
        val data = Paths.get(AppDirsFactory.getInstance().getUserDataDir("Firefox", null, "Mozilla", true))
        val ini: Ini by lazy { Wini(data.resolve("profiles.ini").toFile()) }

        val all: List<Profile>
            get() {
                return ini.entries.filter {
                    it.key.startsWith("Profile")
                }.map {
                    Profile(data.resolve(Paths.get(it.value["Path"]!!)), it.value["Name"]!!)
                }
            }

        //TODO support multiple installs
        //currently gets only first install from ini
        val default: Profile by lazy {
            val dpath = ini[ini.keys.first()]!!["Default"]!!
            all.first { it.path.endsWith(Paths.get(dpath)) }
        }
    }
}