package cc.polarastrum.service

import cc.polarastrum.service.taboolib.common.TabooLibCommon
import cc.polarastrum.service.taboolib.platform.AppIO
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import taboolib.common.platform.function.info


/**
 * ExamplePlugin
 * cc.polarastrum.service.PolarAstrumService
 *
 * @author mical
 * @since 2025/2/7 23:52
 */
object PolarAstrumService : KotlinPlugin(JvmPluginDescription.loadFromResource()) {

    init {
        // 设置插件信息
        AppIO.pluginId = "PolarAstrumService"
        AppIO.pluginVersion = description.version.toString()
        // 设置数据目录
        AppIO.nativeDataFolder = configFolder
        // 初始化 TabooLib
        TabooLibCommon.testSetup()
    }

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }

    override fun onDisable() {
        // 卸载 TabooLib
        TabooLibCommon.testCancel()
    }
}