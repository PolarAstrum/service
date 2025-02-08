package cc.polarastrum.service

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

/**
 * PolarAstrumService
 * cc.polarastrum.service.PolarAstrumService
 *
 * @author mical
 * @since 2025/2/9 01:12
 */
object PolarAstrumService : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }
}