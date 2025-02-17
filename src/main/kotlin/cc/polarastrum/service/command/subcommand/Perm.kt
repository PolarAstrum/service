package cc.polarastrum.service.command.subcommand

import cc.polarastrum.service.data.PluginDataLoader
import cc.polarastrum.service.database.PluginDatabase
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.module.lang.asLangText
import taboolib.module.lang.asLangTextList
import taboolib.module.lang.asLangTextOrNull

/**
 * PolarAstrumService
 * cc.polarastrum.service.command.subcommand.Perm
 *
 * @author mical
 * @since 2025/2/17 21:42
 */
val permSubCommand = subCommand {
    dynamic("operation") {
        suggest {
            listOf(
                "+", "add", "添加",
                "-", "take", "del", "remove", "delete", "删除", "取消",
                "?", "query", "list", "查询"
            )
        }
        dynamic("user") {
            dynamic("plugin", optional = true) {
                execute<ProxyCommandSender> { sender, ctx, _ ->
                    val operation = ctx["operation"]
                    val user = ctx["user"].toLongOrNull()
                    if (user == null) {
                        commandWrong(sender, "perm")
                        return@execute
                    }
                    val plugin = ctx.getOrNull("plugin")
                    val pluginData = PluginDataLoader.registered[plugin?.lowercase()]
                    when (operation) {
                        "+", "add", "添加" -> {
                            if (checkPlugin(sender, plugin)) {
                                if (PluginDatabase.hasPlugin(user, pluginData!!.name)) {
                                    sender.sendMessage("""
                                    ---== 极沫星舱 ==---
                                    该用户已经拥有插件 ${pluginData.name} 的下载权限
                                """.trimIndent())
                                    return@execute
                                }
                                PluginDatabase.addPlugin(pluginData.name, user)
                                sender.sendMessage("""
                                    ---== 极沫星舱 ==---
                                    已将 ${pluginData.name} 下载权限授权给用户 $user
                                """.trimIndent())
                            }
                        }
                        "-", "take", "del", "remove", "delete", "删除", "取消" -> {
                            if (checkPlugin(sender, plugin)) {
                                if (!PluginDatabase.hasPlugin(user, pluginData!!.name)) {
                                    sender.sendMessage("""
                                    ---== 极沫星舱 ==---
                                    该用户没有插件 ${pluginData.name} 的下载权限
                                """.trimIndent())
                                    return@execute
                                }
                                PluginDatabase.removePlugin(pluginData.name, user)
                                sender.sendMessage("""
                                    ---== 极沫星舱 ==---
                                    已取消授权用户 $user 的 ${pluginData.name} 的下载权限
                                """.trimIndent())
                            }
                        }
                        "?", "query", "list", "查询" -> {
                            queryPlugin(sender, user)
                        }
                    }
                }
            }
            execute<ProxyCommandSender> { sender, ctx, _ ->
                val operation = ctx["operation"]
                val user = ctx["user"].toLongOrNull()
                if (user == null) {
                    commandWrong(sender, "perm")
                    return@execute
                }
                when (operation) {
                    "+", "add", "添加", "-", "take", "del", "remove", "delete", "删除", "取消" -> {
                        checkPlugin(sender, null)
                    }
                    "?", "query", "list", "查询" -> {
                        queryPlugin(sender, user)
                    }
                }
            }
        }
    }
}

private fun queryPlugin(sender: ProxyCommandSender, user: Long) {
    val plugins = PluginDatabase.listPlugins(user)
        .mapNotNull { PluginDataLoader.registered[it.lowercase()] }
    if (plugins.isEmpty()) {
        sender.sendMessage("""
            ---== 极沫星舱 ==---
            该用户没有在开发组购买过任何插件
        """.trimIndent())
        return
    }
    printPlugins(plugins, sender, user)
}

private fun checkPlugin(sender: ProxyCommandSender, plugin: String?): Boolean {
    return if (plugin == null || !PluginDataLoader.registered.containsKey(plugin.lowercase())) {
        commandWrong(sender, "perm")
        false
    } else true
}

private fun commandWrong(sender: ProxyCommandSender, command: String) {
    var usage = sender.asLangTextOrNull("command-subCommands-$command-usage") ?: ""
    if (usage.isNotEmpty()) usage += " "
    val description = sender.asLangTextOrNull("command-subCommands-$command-description") ?: sender.asLangText("command-no-desc")
    sender.sendMessage(
        sender.asLangTextList("command-argument-wrong", command to "name", usage to "usage", description to "description")
            .joinToString("\n")
    )
}