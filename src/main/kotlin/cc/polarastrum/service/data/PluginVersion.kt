package cc.polarastrum.service.data

/**
 * PolarAstrumService
 * cc.polarastrum.service.data.PluginVersion
 *
 * @author mical
 * @since 2025/2/9 18:11
 */
data class PluginVersion(
    val name: String,
    val versionId: String,
    val internalId: Int,
    val downloads: Int
)