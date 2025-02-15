package cc.polarastrum.service.taboolib.platform.type

import cc.polarastrum.service.taboolib.platform.MiraiCommand
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.command.CommandSender
import taboolib.common.platform.ProxyCommandSender

/**
 * TabooLib
 * taboolib.platform.type.BukkitConsole
 *
 * @author sky
 * @since 2021/6/17 10:35 下午
 */
class MiraiCommandSender(val sender: CommandSender) : ProxyCommandSender {

    override val origin: Any
        get() = sender

    override val name: String
        get() = sender.name

    override var isOp: Boolean
        get() = true
        set(value) {}

    override fun isOnline(): Boolean {
        return true
    }

    override fun sendMessage(message: String) {
        runBlocking {
            if (message.contains("§")) {
                sender.sendMessage(stripColor(message))
            } else {
                sender.sendMessage(message)
            }
        }
    }

    override fun performCommand(command: String): Boolean {
        MiraiCommand.runCommand(this, command)
        return true
    }

    override fun hasPermission(permission: String): Boolean {
        return true
    }

    fun stripColor(message: String): String {
        val filteredMessage = StringBuilder()
        var skip = false
        for (char in message) {
            if (char == '§') {
                skip = true
            } else if (skip) {
                // 判断 § 后面的东西
                if (char.isLetterOrDigit()) {
                    skip = false
                } else {
                    filteredMessage.append('§').append(char)
                    skip = false
                }
            } else {
                filteredMessage.append(char)
            }
        }
        return filteredMessage.toString()
    }
}