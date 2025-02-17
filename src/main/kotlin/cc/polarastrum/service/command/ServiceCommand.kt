package cc.polarastrum.service.command

import cc.polarastrum.service.command.subcommand.mySubCommand
import cc.polarastrum.service.command.subcommand.permSubCommand
import cc.polarastrum.service.command.subcommand.pluginsSubCommand
import cc.polarastrum.service.command.subcommand.reloadSubCommand
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

/**
 * PolarAstrumService
 * cc.polarastrum.service.command.ServiceCommand
 *
 * @author mical
 * @since 2025/2/15 18:51
 */
@CommandHeader("lab", aliases = ["service", "polarastrum"])
object ServiceCommand {

    @CommandBody
    val main = mainCommand {
        createTabooLegacyHelper()
    }

    @CommandBody
    val my = mySubCommand

    @CommandBody(permission = "admin")
    val perm = permSubCommand

    @CommandBody
    val plugins = pluginsSubCommand

    @CommandBody(permission = "admin")
    val reload = reloadSubCommand
}