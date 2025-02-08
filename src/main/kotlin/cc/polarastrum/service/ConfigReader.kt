package cc.polarastrum.service

import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

/**
 * PolarAstrumService
 * cc.polarastrum.service.ConfigReader
 *
 * @author mical
 * @since 2025/2/9 00:48
 */
object ConfigReader {

    @Config
    lateinit var config: Configuration
        private set
}