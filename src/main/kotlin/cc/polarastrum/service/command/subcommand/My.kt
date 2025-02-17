package cc.polarastrum.service.command.subcommand

import cc.polarastrum.service.data.PluginData
import cc.polarastrum.service.data.PluginDataLoader
import cc.polarastrum.service.database.PluginDatabase
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.buildMessageChain
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand

/**
 * PolarAstrumService
 * cc.polarastrum.service.command.subcommand.My
 *
 * @author mical
 * @since 2025/2/17 21:22
 */
val mySubCommand = subCommand {
    execute<ProxyCommandSender> { sender, _, _ ->
        val plugins = PluginDatabase.listPlugins(sender.name.toLong()).mapNotNull { PluginDataLoader.registered[it] }
        if (plugins.isEmpty()) {
            sender.sendMessage("""
                ---== 极沫星舱 ==---
                你没有在开发组购买过任何插件
            """.trimIndent())
            return@execute
        }
        printPlugins(plugins, sender)
    }
}

internal fun printPlugins(plugins: List<PluginData>, sender: ProxyCommandSender, userId: Long? = null) {
    sender.sendMessage(
        buildMessageChain {
            add("---== 极沫星舱 ==---\n")
            if (userId == null) {
                add("用户 ${sender.castSafely<CommandSender>()?.user?.nameCardOrNick}(${sender.name}) 购买插件如下:\n")
            } else {
                add("用户 $userId 购买插件如下:\n")
            }
            plugins.forEachIndexed { index, plugin ->
                add(" ${index + 1}. ${plugin.name}\n")
                add("    文档: ${plugin.wikiUrl}\n")
            }
            // 为 null 证明是查询自己的
            if (userId == null) {
                add("输入 /lab download [插件名] 来下载插件")
            }
        }.contentToString()
    )
}