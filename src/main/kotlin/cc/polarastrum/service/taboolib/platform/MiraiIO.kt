package cc.polarastrum.service.taboolib.platform

import net.mamoe.mirai.console.MiraiConsole
import taboolib.common.Inject
import taboolib.common.env.RuntimeDependency
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.service.PlatformIO
import java.io.File

/**
 * TabooLib
 * taboolib.platform.AppIO
 *
 * @author sky
 * @since 2021/6/14 11:10 下午
 */
@Awake
@Inject
@PlatformSide(Platform.APPLICATION)
@RuntimeDependency(value = "!org.apache.commons:commons-lang3:3.5", test = "!org.apache.commons.lang3.concurrent.BasicThreadFactory")
class MiraiIO : PlatformIO {

    val isLog4jEnabled by lazy {
        try {
            Class.forName("org.apache.log4j.Logger")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    val plugin: MiraiPlugin
        get() = MiraiPlugin.instance

    override val pluginId: String
        get() = try {
            plugin.description.name
        } catch (ex: Throwable) {
            "application"
        }

    override val pluginVersion: String
        get() = try {
            plugin.description.version.toString()
        } catch (ex: Throwable) {
            "application"
        }

    override val isPrimaryThread: Boolean
        get() = true

    override fun <T> server(): T {
        TODO("Not yet implemented")
    }

    override fun info(vararg message: Any?) {
        message.filterNotNull().forEach {
            if (isLog4jEnabled) {
                println(it)
            } else {
                MiraiConsole.mainLogger.info("[$pluginId] $it")
            }
        }
    }

    override fun severe(vararg message: Any?) {
        message.filterNotNull().forEach {
            if (isLog4jEnabled) {
                println(it)
            } else {
                MiraiConsole.mainLogger.error("[$pluginId] $it")
            }
        }
    }

    override fun warning(vararg message: Any?) {
        message.filterNotNull().forEach {
            if (isLog4jEnabled) {
                println(it)
            } else {
                MiraiConsole.mainLogger.warning("[$pluginId] $it")
            }
        }
    }

    override fun releaseResourceFile(source: String, target: String, replace: Boolean): File {
        val file = File(getDataFolder(), target)
        if (file.exists() && !replace) {
            return file
        }
        newFile(file).writeBytes(javaClass.classLoader.getResourceAsStream(source)?.readBytes() ?: error("resource not found: $source"))
        return file
    }

    override fun getJarFile(): File {
        return File(MiraiIO::class.java.protectionDomain.codeSource.location.toURI().path)
    }

    override fun getDataFolder(): File {
        return plugin.configFolder
    }

    override fun getPlatformData(): Map<String, Any> {
        return emptyMap()
    }
}