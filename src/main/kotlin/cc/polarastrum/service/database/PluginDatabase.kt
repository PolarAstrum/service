package cc.polarastrum.service.database

import cc.polarastrum.service.ConfigReader
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table

/**
 * PolarAstrumService
 * cc.polarastrum.service.database.PluginDatabase
 *
 * @author mical
 * @since 2025/2/9 19:15
 */
object PluginDatabase {

    private val host = HostSQL(ConfigReader.config.getConfigurationSection("Settings.database")!!)

    private val table = Table("polarastrum_service_user", host) {
        add("user") {
            type(ColumnTypeSQL.BIGINT)
        }
        add("plugin") {
            type(ColumnTypeSQL.TEXT)
        }
    }

    private val dataSource = host.createDataSource()

    @Awake(LifeCycle.ENABLE)
    fun init() {
        table.workspace(dataSource) { createTable(true) }.run()
    }

    /**
     * 购买一个插件，为该用户添加下载权限
     */
    fun addPlugin(plugin: String, user: Long) {
        val plugins = listPlugins(user)
        plugins += plugin
        if (plugins.size == 1) {
            table.insert(dataSource, "user", "plugin") {
                value(user, plugins.joinToString(","))
            }
        } else {
            table.update(dataSource) {
                set("plugin", plugins.joinToString(","))
                where {
                    "user" eq user
                }
            }
        }
    }

    fun removePlugin(plugin: String, user: Long) {
        val last = listPlugins(user)
        last.remove(plugin)
        if (last.isEmpty()) {
            table.delete(dataSource) {
                where {
                    "user" eq user
                }
            }
        }
    }

    fun listPlugins(user: Long): MutableSet<String> {
        return table.select(dataSource) {
            where {
                "user" eq user
            }
        }.firstOrNull { getString("plugin").splitToMutableSet() } ?: mutableSetOf()
    }

    fun hasPlugin(user: Long, plugin: String): Boolean {
        return plugin in listPlugins(user)
    }

    private fun addPlugin(addPlugin: String, plugins: String): String {
        return plugins.splitToMutableSet().also { it.add(addPlugin.trim()) }.joinToString(",")
    }

    private fun removePlugin(removePlugin: String, plugins: String): String {
        return plugins.splitToMutableSet().also { it.remove(removePlugin.trim()) }.joinToString(",")
    }

    private fun checkPlugin(checkPlugin: String, plugins: String): Boolean {
        return checkPlugin in plugins.splitToMutableSet()
    }
}

    private fun String.splitToMutableSet(): MutableSet<String> {
        return if (isEmpty()) mutableSetOf()
        else this.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toMutableSet()
}