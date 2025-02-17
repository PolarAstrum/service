package cc.polarastrum.service.command.subcommand

import cc.polarastrum.service.ConfigReader
import cc.polarastrum.service.data.PluginDataLoader
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.module.lang.Language

/**
 * PolarAstrumService
 * cc.polarastrum.service.command.subcommand.Reload
 *
 * @author mical
 * @since 2025/2/15 22:09
 */
val reloadSubCommand = subCommand {
    execute<ProxyCommandSender> { sender, _, _ ->
        val time = System.currentTimeMillis()
        ConfigReader.config.reload()
        PluginDataLoader.reload()
        Language.reload()
        sender.sendMessage("""
            ---== 极沫星舱 ==---
            插件已重载，耗时 ${System.currentTimeMillis() - time} ms
        """.trimIndent())
    }
}