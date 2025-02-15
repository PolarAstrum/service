package cc.polarastrum.service.command

import cc.polarastrum.service.command.subcommand.pluginsSubCommand
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.expansion.createHelper

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
        createHelper()
    }

    @CommandBody
    val plugins = pluginsSubCommand
}