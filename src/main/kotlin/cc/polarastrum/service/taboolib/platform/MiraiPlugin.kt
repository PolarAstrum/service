package cc.polarastrum.service.taboolib.platform

import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import taboolib.common.LifeCycle
import taboolib.common.PrimitiveIO
import taboolib.common.TabooLib
import taboolib.common.classloader.IsolatedClassLoader
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.submit
import taboolib.common.util.t

/**
 * ExamplePlugin
 * cc.polarastrum.service.taboolib.platform.PolarAstrumService
 *
 * @author mical
 * @since 2025/2/7 23:52
 */
class MiraiPlugin : KotlinPlugin(JvmPluginDescription.loadFromResource("plugin.yml")) {

    companion object {

        private var pluginInstance: Plugin? = null
        internal lateinit var instance: MiraiPlugin

        init {
            try {
                // 初始化 IsolatedClassLoader
                IsolatedClassLoader.init(MiraiPlugin::class.java)
            } catch (ex: Throwable) {
                TabooLib.setStopped(true)
                PrimitiveIO.error(
                    """
                        无法初始化原始加载器，插件 "{0}" 将被禁用！
                        Failed to initialize primitive loader, the plugin "{0}" will be disabled!
                    """.t(),
                    PrimitiveIO.getRunningFileName()
                )
                throw ex
            }
            // 生命周期任务
            TabooLib.lifeCycle(LifeCycle.CONST)
            // 检查 TabooLib Plugin 实现
            pluginInstance = Plugin.getInstance()
        }
    }

    init {
        instance = this
        // 生命周期任务
        TabooLib.lifeCycle(LifeCycle.INIT)
    }

    override fun PluginComponentStorage.onLoad() {
        // 生命周期任务
        TabooLib.lifeCycle(LifeCycle.LOAD)
        // 调用 Plugin 实现的 onLoad 方法
        // FIXME 暂时无法访问 PluginComponentStorage
        if (!TabooLib.isStopped()) {
            pluginInstance?.onLoad()
        }
    }

    override fun onEnable() {
        // 生命周期任务
        TabooLib.lifeCycle(LifeCycle.ENABLE)
        // 判断插件是否关闭
        if (!TabooLib.isStopped()) {
            pluginInstance?.onEnable()
        }
        // 再次判断插件是否关闭
        // 因为插件可能在 onEnable() 下关闭
        if (!TabooLib.isStopped()) {
            // FIXME ENABLE 和 ACTIVE 生命周期同时进行
            submit {
                // 生命周期任务
                TabooLib.lifeCycle(LifeCycle.ACTIVE)
                // 调用 Plugin 实现的 onActive() 方法
                pluginInstance?.onActive()
            }
        }
    }

    override fun onDisable() {
        // 在插件未关闭的情况下, 执行 onDisable() 方法
        if (!TabooLib.isStopped()) {
            pluginInstance?.onDisable()
        }
        // 生命周期任务
        TabooLib.lifeCycle(LifeCycle.DISABLE)
    }
}