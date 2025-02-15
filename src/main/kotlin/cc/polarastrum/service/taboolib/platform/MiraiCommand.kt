package cc.polarastrum.service.taboolib.platform

import cc.polarastrum.service.taboolib.platform.type.MiraiCommandSender
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.GroupTempMessageEvent
import net.mamoe.mirai.event.events.StrangerMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import taboolib.common.Inject
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandCompleter
import taboolib.common.platform.command.CommandExecutor
import taboolib.common.platform.command.CommandStructure
import taboolib.common.platform.command.component.CommandBase
import taboolib.common.platform.function.registerLifeCycleTask
import taboolib.common.platform.service.PlatformCommand

/**
 * TabooLib
 * taboolib.platform.BukkitCommand
 *
 * @author sky
 * @since 2021/6/26 2:33 下午
 */
@Awake
@Inject
@PlatformSide(Platform.APPLICATION)
class MiraiCommand : PlatformCommand {

    companion object {

        var commandPrefix: String = "/"

        val unknownCommandMessage: String
            get() = System.getProperty("taboolib.application.command.unknown.message") ?: "Unknown command."

        val commands = mutableSetOf<Command>()

        fun register(command: Command) {
            commands.add(command)
        }

        fun unregister(name: String) {
            commands.find { it.command.aliases.contains(name) } ?: return
            unregister(name)
        }

        fun unregister(command: Command) {
            commands.remove(command)
        }

        fun runCommand(sender: ProxyCommandSender, cmd: String) {
            if (cmd.isBlank()) {
                return
            }
            val content = cmd.removePrefix(commandPrefix)
            val label = if (content.contains(" ")) content.substringBefore(" ") else content
            val command = commands.find { it.aliases.contains(label) } ?: return sender.sendMessage(unknownCommandMessage)
            val args = if (content.contains(" ")) content.substringAfter(" ").split(" ") else listOf()
            command.executor.execute(sender, command.command, label, args.toTypedArray())
        }
    }

    data class Command(val command: CommandStructure, val executor: CommandExecutor, val completer: CommandCompleter, val commandBuilder: CommandBase.() -> Unit) {

        val aliases get() = listOf(command.name, *command.aliases.toTypedArray())

        fun register() = register(this)

        fun unregister() = unregister(this)
    }

    init {
        registerLifeCycleTask(LifeCycle.LOAD) {
            val plugin = MiraiPlugin.instance

            fun perform(message: String, sender: CommandSender) {
                if (!message.startsWith(commandPrefix)) {
                    return
                }
                MiraiCommandSender(sender).performCommand(message)
            }

            plugin.globalEventChannel().subscribeAlways<FriendMessageEvent> { e ->
                perform(e.message.contentToString(), e.toCommandSender())
            }
            plugin.globalEventChannel().subscribeAlways<GroupMessageEvent> { e ->
                perform(e.message.contentToString(), e.toCommandSender())
            }
            plugin.globalEventChannel().subscribeAlways<GroupTempMessageEvent> { e ->
                perform(e.message.contentToString(), e.toCommandSender())
            }
            plugin.globalEventChannel().subscribeAlways<StrangerMessageEvent> { e ->
                perform(e.message.contentToString(), e.toCommandSender())
            }
        }
    }

    override fun registerCommand(command: CommandStructure, executor: CommandExecutor, completer: CommandCompleter, commandBuilder: CommandBase.() -> Unit) {
        register(Command(command, executor, completer, commandBuilder))
    }

    override fun unknownCommand(sender: ProxyCommandSender, command: String, state: Int) {
        sender.sendMessage("$command<--[HERE]")
    }

    override fun unregisterCommand(command: String) {
        unregister(commands.find { it.command.aliases.contains(command) } ?: return)
    }

    override fun unregisterCommands() {
        commands.forEach { unregister(it) }
    }
}