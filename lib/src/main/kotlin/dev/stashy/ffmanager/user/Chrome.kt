package dev.stashy.ffmanager.user

import dev.stashy.ffmanager.`package`.ChromePackage
import java.nio.file.Path

class Chrome(var path: Path) {

    constructor(profile: Profile) : this(profile.root.resolve("chrome"))

    private val style: Styles by lazy { Styles(this) }
    //private val script: ScriptManager by lazy { ScriptManager(this) }

    val enabled: List<ChromePackage>
    get() {
        return style.mapNotNull { try { ChromePackage.from(path.resolve(it.parent)) } catch (e: Exception) { null } }
    }

    fun enable(pkg: ChromePackage) {
        pkg.path?.let {
            style.add(it.resolve("userChrome.css"))
            style.flush()
        }
    }

    fun disable(pkg: ChromePackage) {
        pkg.path?.let {
            style.remove(it.resolve("userChrome.css"))
            style.flush()
        }
    }

}