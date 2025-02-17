package cc.polarastrum.service.command.subcommand

import cc.polarastrum.service.data.PluginDataLoader
import net.mamoe.mirai.message.data.buildMessageChain
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand

/**
 * PolarAstrumService
 * cc.polarastrum.service.command.subcommand.Plugins
 *
 * @author mical
 * @since 2025/2/15 19:03
 */
val pluginsSubCommand = subCommand {
    dynamic("plugin", optional = true) {
        execute<ProxyCommandSender> { sender, ctx, _ ->
            val plugin = PluginDataLoader.registered[ctx["plugin"].lowercase()]
            if (plugin == null) {
                sender.sendMessage("""
                    ---== 极沫星舱 ==---
                    发的什么玩意，找不到你要的插件
                """.trimIndent())
                return@execute
            }
            sender.sendMessage("""
                ---== 极沫星舱 ==---
                插件 ${plugin.name}:
                  价格: ${plugin.price}
                  文档: ${plugin.wikiUrl}
            """.trimIndent())
        }
    }
    execute<ProxyCommandSender> { sender, _, _ ->
        sender.sendMessage(
            buildMessageChain {
                add("---== 极沫星舱 ==---\n")
                add("在售插件如下:\n")
                PluginDataLoader.registered.values.forEachIndexed { index, plugin ->
                    add(" ${index + 1}. ${plugin.name}\n")
                    add("    价格: ${plugin.price}\n")
                    add("    文档: ${plugin.wikiUrl}\n")
                }
            }.contentToString()
        )
    }
}