package cc.polarastrum.service.data

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.common.platform.function.registerLifeCycleTask
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import java.util.concurrent.ConcurrentHashMap

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.core.data.Target
 *
 * @author mical
 * @since 2024/2/17 23:32
 */
data class PluginData(
    private val root: ConfigurationSection,
    val name: String = root.getString("name")!!,
    val price: Double = root.getDouble("price"),
    val wikiUrl: String = root.getString("wiki-url", "https://wiki.polarastrum.cc")!!,
    val inject: Boolean = root.getBoolean("inject"),
    val injectClass: String = root.getString("inject-class", "")!!,
    val injectField: String = root.getString("inject-field", "")!!,
)

object PluginDataLoader {

    @Config("plugins.yml", autoReload = true)
    lateinit var config: Configuration
        private set

    val registered: ConcurrentHashMap<String, PluginData> = ConcurrentHashMap()

    private var isLoaded = false

    @Awake(LifeCycle.ENABLE)
    fun init() {
        if (isLoaded) {
            config.reload()
            return
        }
        load()
        isLoaded = true
    }

    @Awake(LifeCycle.ENABLE)
    fun reload() {
        registerLifeCycleTask(LifeCycle.ENABLE) {
            config.onReload {
                load()
            }
        }
    }

    private fun load() {
        val time = System.currentTimeMillis()
        registered.clear()
        for (section in config.getKeys(false).map { config.getConfigurationSection(it)!! }) {
            val data = PluginData(section)
            registered += data.name to data
        }
        info("Loaded ${registered.size} plugins in ${System.currentTimeMillis() - time} ms")
    }
}
