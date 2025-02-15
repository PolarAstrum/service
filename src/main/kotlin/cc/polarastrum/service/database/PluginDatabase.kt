package cc.polarastrum.service.database

import cc.polarastrum.service.ConfigReader
import taboolib.module.database.HostSQL

/**
 * PolarAstrumService
 * cc.polarastrum.service.database.PluginDatabase
 *
 * @author mical
 * @since 2025/2/9 19:15
 */
object PluginDatabase {

    private val host = HostSQL(ConfigReader.config.getConfigurationSection("Setting.database")!!)

}