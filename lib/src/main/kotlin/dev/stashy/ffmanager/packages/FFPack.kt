package dev.stashy.ffmanager.packages

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import dev.stashy.ffmanager.user.Profile
import sun.plugin.dom.exception.InvalidAccessException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KProperty

interface FFPack {
    val id: String

    fun uninstall(profile: Profile) = profile.chrome.uninstall(this)

    fun enable(profile: Profile) = profile.chrome.enable(this)

    fun disable(profile: Profile) = profile.chrome.disable(this)

    companion object {
        fun from(path: Path): FFPack? {
            if (!Files.isDirectory(path))
                return null
            val metaPath = path.resolve("meta.json")
            return if (Files.exists(metaPath))
                object : PackFiles, PackMeta, FFPack {
                    override val id: String by PackId()
                    override val meta by lazy { Klaxon().parseJsonObject(Files.newBufferedReader(metaPath)) }
                    override val path = path
                }
            else
                object : PackFiles, FFPack {
                    override val id: String by PackId()
                    override val path = path
                }
        }
    }
}

interface PackFiles : FFPack {
    val path: Path

    fun install(profile: Profile) = profile.chrome.install(this)
}

interface PackMeta : FFPack {
    val meta: JsonObject
    val name: String?
        get() = meta.string("name")
    val description: String?
        get() = meta.string("description")
    val version: String?
        get() = meta.string("version")
    val compatible: List<String>?
        get() = meta.array("compatible")
    val updateUrl: String?
        get() = meta.string("updateUrl")
}

class PackId {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return when (thisRef) {
            is PackMeta -> thisRef.meta.string("id")!!
            is PackFiles -> thisRef.path.fileName.toString()
            else -> throw InvalidAccessException("MetaId must be called from PackMeta")
        }
    }
}