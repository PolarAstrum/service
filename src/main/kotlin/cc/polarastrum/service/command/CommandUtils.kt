package cc.polarastrum.service.command

import cc.polarastrum.service.util.variable
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.component.CommandBase
import taboolib.common.platform.command.component.CommandComponent
import taboolib.common.platform.command.component.CommandComponentLiteral
import taboolib.common.platform.function.pluginVersion
import taboolib.common.util.Strings
import taboolib.module.lang.asLangText
import taboolib.module.lang.asLangTextList
import taboolib.module.lang.asLangTextOrNull

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.module.ingame.command.CommandUtils
 *
 * @author mical
 * @since 2024/10/6 01:02
 */
@Suppress("DuplicatedCode")
fun CommandComponent.createTabooLegacyHelper(commandType: String = "main", main: Boolean = commandType == "main") {
    val prefix = "command" + if (main) "" else "-$commandType"
    execute<ProxyCommandSender> { sender, _, _ ->
        val text = mutableListOf<String>()

        for (command in children.filterIsInstance<CommandComponentLiteral>()) {
            if (!sender.isOp) {
                if (!sender.hasPermission(command.permission)) continue
                else if (command.hidden) continue
            }
            val name = command.aliases[0]
            var usage = sender.asLangTextOrNull("$prefix-subCommands-$name-usage") ?: ""
            if (usage.isNotEmpty()) usage += " "
            val description = sender.asLangTextOrNull("$prefix-subCommands-$name-description") ?: sender.asLangText("$prefix-no-desc")
            text += sender.asLangTextList("$prefix-sub", name to "name", description to "description", usage to "usage")
        }

        sender.sendMessage(
            sender.asLangTextList(
                "$prefix-helper",
                pluginVersion to "pluginVersion"
            ).variable("subCommands", text).joinToString("\n")
        )
    }

    if (this is CommandBase) {
        incorrectCommand { sender, ctx, _, state ->
            val input = ctx.args().first()
            val name = children.filterIsInstance<CommandComponentLiteral>()
                .firstOrNull { it.aliases.contains(input) }?.aliases?.get(0) ?: input
            var usage = sender.asLangTextOrNull("$prefix-subCommands-$name-usage") ?: ""
            if (usage.isNotEmpty()) usage += " "
            val description = sender.asLangTextOrNull("$prefix-subCommands-$name-description") ?: sender.asLangText("$prefix-no-desc")

            when (state) {
                // 缺参数
                1 -> {
                    sender.sendMessage(
                        sender.asLangTextList("$prefix-argument-missing", name to "name", usage to "usage", description to "description")
                            .joinToString("\n")
                    )
                }

                // 参数错误
                2 -> {
                    if (ctx.args().size > 1) {
                        sender.sendMessage(
                            sender.asLangTextList("$prefix-argument-wrong", name to "name", usage to "usage", description to "description")
                                .joinToString("\n")
                        )
                    } else {
                        val similar = children.filterIsInstance<CommandComponentLiteral>()
                            .filterNot { it.hidden }
                            .filter { sender.hasPermission(it.permission) }
                            .maxByOrNull { Strings.similarDegree(name, it.aliases[0]) }!!
                            .aliases[0]
                        sender.sendMessage(
                            sender.asLangTextList("$prefix-argument-unknown", name to "name", similar to "similar")
                                .joinToString("\n")
                        )
                    }
                }
            }
        }

        incorrectSender { sender, ctx ->
            sender.sendMessage(
                sender.asLangTextList("$prefix-incorrect-sender", ctx.args().first() to "name")
                    .joinToString("\n")
            )
        }
    }
}