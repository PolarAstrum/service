package cc.polarastrum.service

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.lang.Language

/**
 * PolarAstrumService
 * cc.polarastrum.service.PolarAstrumService
 *
 * @author mical
 * @since 2025/2/9 01:12
 */
object PolarAstrumService : Plugin() {

    @Awake(LifeCycle.INIT)
    fun init() {
        Language.enableFileWatcher = true
        Language.releasePath = "config/cc.polarastrum.service/lang/{1}"
    }

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }
}